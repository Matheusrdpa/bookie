package com.estudo.bookie.services.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private String secret = "IASUHIUASHFUASHIASUIHASIUDHAIUSDHIAJNSHUDHNASUDHNUAISDNUISDHNAUSHDNISDHAUSISDUHD";

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 2))
                .signWith(getSecretKey())
                .compact();
    }

    private Key getSecretKey() {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(encodedKey);
    }

}
