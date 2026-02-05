package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.AuthDto;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setup(){
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        bookRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@bookie.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRoles(Set.of("ADMIN", "USER"));
        userRepository.save(admin);

        AuthDto loginDto = new AuthDto("admin","123456");
        adminToken = given().contentType(ContentType.JSON)
                .body(loginDto)
        .when()
                .post("v1/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    @Test
    @DisplayName("Should successfully create book when user is admin")
    void createBook_Should_Return_201_When_Admin(){
        AuthorRequestDto authorRequestDto = new AuthorRequestDto(null, "Brandon Sanderson","bio",null);
        Integer authorId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(authorRequestDto)
                .when()
                .post("/v1/author")
                .then()
                .statusCode(201)
                .extract().path("id");

        BookRequestDto bookRequestDto = new BookRequestDto(null, "Mistborn",authorId.longValue(),5.0,"Fantasy","desc");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(bookRequestDto)
                .when()
                .post("/v1/book")
                .then()
                .statusCode(201)
                .body("title", equalTo("Mistborn"))
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Should fail to create book when user is not admin")
    void Createbook_Should_Fail_When_Not_Admin(){
        User common = new User();
        common.setUsername("common");
        common.setRoles(Set.of("user"));
        common.setEmail("common@bookie.com");
        common.setPassword("123456");
        userRepository.save(common);

        String userToken = given()
                .contentType(ContentType.JSON)
                .body(new AuthDto("common", "123456"))
                .when()
                .post("v1/auth/login")
                .then().extract().asString();

        BookRequestDto bookRequestDto = new BookRequestDto(null, "Mistborn",1L,5.0,"Fantasy","desc");
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(bookRequestDto)
                .when()
                .post("v1/book")
                .then()
                .statusCode(403);
    }

    @Test
    void Createbook_Should_Fail_When_No_Token(){
        BookRequestDto bookRequestDto = new BookRequestDto(null, "Mistborn",1L,5.0,"Fantasy","desc");

        given()
                .contentType(ContentType.JSON)
                .body(bookRequestDto)
                .when()
                .post("v1/book")
                .then()
                .statusCode(403);
    }
}