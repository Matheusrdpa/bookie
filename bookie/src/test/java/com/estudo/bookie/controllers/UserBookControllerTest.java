package com.estudo.bookie.controllers;

import com.estudo.bookie.configuration.SecurityConfig;
import com.estudo.bookie.entities.BookStatus;
import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.UpdateRatingRequest;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.services.UserBookService;
import com.estudo.bookie.services.UserDetailService;
import com.estudo.bookie.services.exceptions.DuplicateResource;
import com.estudo.bookie.services.jwt.JwtFilter;
import com.estudo.bookie.services.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserBookController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtFilter.class})
public class UserBookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserBookService userBookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean(name = "userDetailsService")
    private UserDetailService userDetailService;

    private Authentication userAuth;

    private Authentication user2Auth;

    @BeforeEach
    void setup(){
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("123");
        user.setRoles(Set.of("USER"));
        user.setEmail("user@bookie");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        this.userAuth = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        User user2 = new User();
        user2.setId(3L);
        user2.setUsername("user2");
        user2.setPassword("123");
        user2.setRoles(Set.of("USER"));
        user2.setEmail("user2@bookie");

        CustomUserDetails user2Details = new CustomUserDetails(user2);
        this.user2Auth = new UsernamePasswordAuthenticationToken(user2Details,null,user2Details.getAuthorities());
    }

    @Test
    void addBook_Should_Return_Book_When_Added() throws Exception {
        UserBookResponseDto userBook = new UserBookResponseDto(1L,"New book", 4.5,"desc", BookStatus.READ);
        UserBookRequestDto userbookrequest = new UserBookRequestDto(1L, 3.4,BookStatus.READING);
        when(userBookService.addBookToUserLibrary(eq(1L),any(UserBookRequestDto.class))).thenReturn(userBook);

        mockMvc.perform(post("/v1/userbook")
                .with(authentication(userAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userbookrequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("New book"));
    }

    @Test
    void Should_Return_DuplicateException_When_Already_Exists() throws Exception {

        UserBookRequestDto userbookrequest = new UserBookRequestDto(1L, 3.4,BookStatus.READING);

        when(userBookService.addBookToUserLibrary(eq(1L),any(UserBookRequestDto.class))).thenThrow(DuplicateResource.class);

        mockMvc.perform(post("/v1/userbook")
                        .with(authentication(userAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userbookrequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserLibrary_ShouldReturnPage() throws Exception {
        UserBookResponseDto dto = new UserBookResponseDto(1L, "Clean Code", 5.0, "Tech", BookStatus.READING);

        Page<UserBookResponseDto> page = new PageImpl<>(List.of(dto));

        when(userBookService.getUserLibrary(eq("user"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/v1/userbook/user/books")
                        .with(authentication(userAuth))
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void removeBook_Should_Return_NoContent_When_Owner() throws Exception {

        Long bookId = 10L;
        Long userId = 1L;

        doNothing().when(userBookService).deleteBookFromUserLibrary(eq(userId), eq(bookId));

        mockMvc.perform(delete("/v1/userbook/user/books/{bookId}", bookId)
                        .with(authentication(userAuth)))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeBook_Should_Return_Forbidden_When_Intruder() throws Exception {
        Long bookId = 10L;

        mockMvc.perform(delete("/v1/userbook/user/books/{bookId}", bookId)
                        .with(authentication(user2Auth)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_Should_Return_Dto_When_Exists_And_Owner() throws Exception {
        Long bookId = 5L;
        Long userId = 1L;

        UserBookResponseDto dto = new UserBookResponseDto(null,"title",4.0,"desc",BookStatus.READING);

        when(userBookService.updateUserBook(eq(userId),eq(bookId),anyDouble())).thenReturn(dto);

        mockMvc.perform(patch("/v1/userbook/user/books/{bookId}", bookId)
                        .with(authentication(userAuth)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new UpdateRatingRequest(4.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.0));
    }

    @Test
    void update_Should_Return_Forbidden_When_Is_Not_Owner() throws Exception {
        Long bookId = 5L;

        mockMvc.perform(patch("/v1/userbook/user/books/{bookId}", bookId)
                        .with(authentication(user2Auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateRatingRequest(4.0))))
                .andExpect(status().isForbidden());
    }
}
