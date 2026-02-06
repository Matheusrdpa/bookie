package com.estudo.bookie.controllers;

import com.estudo.bookie.configuration.SecurityConfig;
import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.services.AuthorService;
import com.estudo.bookie.services.UserDetailService;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.jwt.JwtFilter;
import com.estudo.bookie.services.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

@WebMvcTest(controllers = AuthorController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtFilter.class})
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean(name = "userDetailsService")
    private UserDetailService userDetailService;

    private Authentication adminAuth;
    private Authentication userAuth;

    @BeforeEach
    void setUp() {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("123");
        admin.setRoles(Set.of("ADMIN", "USER"));
        CustomUserDetails adminDetails = new CustomUserDetails(admin);
        this.adminAuth = new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());

        User common = new User();
        common.setId(2L);
        common.setUsername("user");
        common.setPassword("123");
        common.setRoles(Set.of("USER"));
        CustomUserDetails userDetails = new CustomUserDetails(common);
        this.userAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void findAll_Should_Return_List() throws Exception {
        List<AuthorRequestDto> authorRequestDtos = List.of(new AuthorRequestDto(1L, "George R.R. Martin", "got",null),new AuthorRequestDto(2L, "Brandon Sanderson", "mb",null));
        when(authorService.findAll()).thenReturn(authorRequestDtos);

        mockMvc.perform(get("/v1/author").with(authentication(userAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("George R.R. Martin"));
    }
    @Test
    void Save_ShouldCreateAuthor_When_Admin() throws Exception {
        AuthorRequestDto inputDto = new AuthorRequestDto(null, "Tolkien", "lotr",null);
        AuthorRequestDto outputDto = new AuthorRequestDto(1L, "Tolkien", "lotr",null);

       when(authorService.save(any(AuthorRequestDto.class))).thenReturn(outputDto);

       mockMvc.perform(post("/v1/author")
               .with(authentication(adminAuth))
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(inputDto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.name").value("Tolkien"));;
    }

    @Test
    void findById_Should_Return_Dto_When_Exists() throws Exception {

        AuthorRequestDto author = new AuthorRequestDto(1L, "George R.R. Martin", "got",null);

        when(authorService.findById(anyLong())).thenReturn(author);

        mockMvc.perform(get("/v1/author/1")
                .with(authentication(userAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("George R.R. Martin"));
    }

    @Test
    void findById_Should_Return_NotFoundException_When_DoesnotExists() throws Exception {
        when(authorService.findById(anyLong())).thenThrow(ResourceNotFound.class);

        mockMvc.perform(get("/v1/author/1")
                        .with(authentication(userAuth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_Should_Return_Dto_When_Exists() throws Exception {
        AuthorRequestDto updated = new AuthorRequestDto(null,"Updated author", "bio", null);

        when(authorService.update(anyLong(),any())).thenReturn(updated);

        mockMvc.perform(put("/v1/author/1")
                        .with(authentication(adminAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated author"))
        ;
    }

    @Test
    void update_Should_Return_NotFoundException_When_DoesnotExists() throws Exception {
        when(authorService.update(anyLong(),any())).thenThrow(ResourceNotFound.class);

        AuthorRequestDto updated = new AuthorRequestDto(null,"Updated author", "bio", null);

        mockMvc.perform(put("/v1/author/1")
                        .with(authentication(adminAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturn403_WhenNotAdmin() throws Exception {
              mockMvc.perform(delete("/v1/author/1")
                        .with(authentication(userAuth)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_ShouldDelete_WhenAdmin() throws Exception {
        doNothing().when(authorService).delete(1L);

        mockMvc.perform(delete("/v1/author/1")
                        .with(authentication(adminAuth)))

                .andExpect(status().isNoContent());
    }
}
