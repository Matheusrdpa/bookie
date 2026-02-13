package com.estudo.bookie.services.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_SECRET = "bWluaGEtY2hhdmUtc2VjcmV0YS1zdXBlci1zZWd1cmEtcGFyYS10ZXN0ZXMtdW5pdGFyaW9zLTEyMw==";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
    }

    @Test
    void generateToken_Should_Return_Non_Null_Token() {
        when(userDetails.getUsername()).thenReturn("User");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_Should_Return_Correct_Username() {
        when(userDetails.getUsername()).thenReturn("User");
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("User", username);
    }

    @Test
    void validateToken_Should_Return_True_When_Token_Is_Valid() {
        when(userDetails.getUsername()).thenReturn("User");
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_Should_Return_False_When_User_Does_Not_Match() {
        when(userDetails.getUsername()).thenReturn("User");
        String token = jwtService.generateToken(userDetails);


        UserDetails notUser = Mockito.mock(UserDetails.class);
        when(notUser.getUsername()).thenReturn("notUser");

        assertFalse(jwtService.validateToken(token, notUser));
    }

    @Test
    void extractUsername_Should_Throw_ExpiredJwtException_When_Token_Expired() {
        byte[] keyBytes = Base64.getDecoder().decode(TEST_SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .subject("User")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(expiredToken));
    }
}