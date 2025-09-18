package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.services.UserBookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/userbook")
public class UserBookController {
    private UserBookService userBookService;
    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserBookResponseDto> createUserBook(@AuthenticationPrincipal CustomUserDetails loggedUser, @Valid @RequestBody UserBookRequestDto userBookRequestDto) {
        UserBookResponseDto userBookResponseDto = userBookService.addBookToUserLibrary(loggedUser.getId(), userBookRequestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userBookResponseDto.bookId()).toUri();
        return ResponseEntity.created(uri).body(userBookResponseDto);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteUserBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails loggedUser) {
        userBookService.deleteBookFromUserLibrary(loggedUser.getId(), bookId);
        return ResponseEntity.noContent().build();
    }
}
