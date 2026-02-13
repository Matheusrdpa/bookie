package com.estudo.bookie.controllers.ExceptionHandler;

import com.estudo.bookie.configuration.SecurityConfig;
import com.estudo.bookie.controllers.BookController;
import com.estudo.bookie.controllers.UserBookController;
import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.services.BookService;
import com.estudo.bookie.services.UserBookService;
import com.estudo.bookie.services.UserDetailService;
import com.estudo.bookie.services.exceptions.DataIntegrityException;
import com.estudo.bookie.services.exceptions.DuplicateResource;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.jwt.JwtFilter;
import com.estudo.bookie.services.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(controllers = {BookController.class, UserBookController.class})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtFilter.class})
public class ResourceExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserBookService userBookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean(name = "userDetailsService")
    private UserDetailService userDetailService;

    private Authentication auth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("test");
        fakeUser.setPassword("123");
        fakeUser.setRoles(Set.of("USER"));

        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("123");
        admin.setRoles(Set.of("ADMIN", "USER"));
        CustomUserDetails adminDetails = new CustomUserDetails(admin);
        this.adminAuth = new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());

        CustomUserDetails userDetails = new CustomUserDetails(fakeUser);

        this.auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void Should_Return_NotFoundJson_When_ResourceNotFoundException_Is_Thrown() throws Exception {
        when(bookService.findById(anyLong())).thenThrow(new ResourceNotFound("Book not found"));

        mockMvc.perform(get("/v1/book/1").with(authentication(auth))

                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Book not found"));
    }

    @Test
    void Should_Return_ErrorJson_When_DataIntegrityException_Is_Thrown() throws Exception {
       doThrow(new DataIntegrityException("Dependant entity")).when(bookService).delete(1L);

        mockMvc.perform(delete("/v1/book/1").with(authentication(adminAuth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Dependant entity"));
    }

    @Test
    void Should_Return_ErrorJson_When_DuplicateResourceException_Is_Thrown() throws Exception {

        when(userBookService.addBookToUserLibrary(any(),any())).thenThrow(new DuplicateResource("Duplicate"));

        String jsonBody = """
                {
                    "bookId": 1,
                    "rating": 5.0,
                    "status": "READING"
                }
                """;

        mockMvc.perform(post("/v1/userbook")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate"));
    }
}
