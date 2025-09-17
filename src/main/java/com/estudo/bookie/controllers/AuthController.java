package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.AuthDto;
import com.estudo.bookie.entities.dtos.UserRequestDto;
import com.estudo.bookie.entities.dtos.UserResponseDto;
import com.estudo.bookie.services.UserService;
import com.estudo.bookie.services.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtService jwtService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.save(userRequestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userResponseDto.id()).toUri();
        return ResponseEntity.created(uri).body(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthDto authDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.username(),authDto.password())
        );
        if(authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}
