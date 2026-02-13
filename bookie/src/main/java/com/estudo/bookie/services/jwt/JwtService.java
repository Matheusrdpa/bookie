package com.estudo.bookie.services.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${my-secret-key}")
    private String secret;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 2))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date getExpirationDate(String token) {
        return getAllClaims(token).getExpiration();
    }

    private SecretKey getSecretKey() {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(encodedKey);
    }

}
