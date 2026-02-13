package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.CustomUserDetails;

import com.estudo.bookie.entities.dtos.UpdateRatingRequest;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.services.UserBookService;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @GetMapping("/{username}/books")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserBookResponseDto>> getUserLibrary(@PathVariable String username,@PageableDefault(size = 10, sort = "id") Pageable pageable) {
       Page<UserBookResponseDto> page = userBookService.getUserLibrary(username, pageable);
       return ResponseEntity.ok(page);
    }

    @PatchMapping("/{username}/books/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserBookResponseDto> updateBookRating(@PathVariable String username,@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails loggedUser, @RequestBody UpdateRatingRequest ratingRequest) {
        String loggedUsername = loggedUser.getUsername();
        if (!loggedUsername.equalsIgnoreCase(username)) {
            throw new AccessDeniedException("Você só pode alterar sua própria biblioteca!");
        }
        return ResponseEntity.ok(userBookService.updateUserBook(loggedUser.getId(),bookId, ratingRequest.rating()));
    }

    @DeleteMapping("/{username}/books/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeBookFromLibrary(@PathVariable String username, @PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails loggedUser) {
        String loggedUsername = loggedUser.getUsername();
        if (!loggedUsername.equalsIgnoreCase(username)) {
            throw new AccessDeniedException("Você só pode alterar sua própria biblioteca!");
        }
        userBookService.deleteBookFromUserLibrary(loggedUser.getId(),bookId);
        return ResponseEntity.noContent().build();
    }
}
