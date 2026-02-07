package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.AuthDto;
import com.estudo.bookie.entities.dtos.UserRequestDto;
import com.estudo.bookie.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save an user successfully")
    void register_Should_Create_User() {
        UserRequestDto registerDto = new UserRequestDto(
                null,
                "new",
                "new@bookie.com",
                "123456"
        );

        given()
                .contentType(ContentType.JSON)
                .body(registerDto)
                .when()
                .post("/v1/auth/register")
                .then()
                .statusCode(201);

        User savedUser = userRepository.findByUsername("new").orElse(null);
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals("new@bookie.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("Shold log in and return token")
    void login_Should_Return_Token_When_Credentials_Valid() {
        User user = new User();
        user.setUsername("reader");
        user.setEmail("reader@bookie.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of("USER"));
        userRepository.save(user);

        AuthDto loginDto = new AuthDto("reader", "123456");

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    @DisplayName("Should fail to log in (403) with wrong password")
    void login_Should_Fail_When_Password_Invalid() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@bookie.com");
        user.setPassword(passwordEncoder.encode("correctpass"));
        user.setRoles(Set.of("USER"));
        userRepository.save(user);

        AuthDto loginDto = new AuthDto("user", "incorrect_pass");

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(403);
    }
}