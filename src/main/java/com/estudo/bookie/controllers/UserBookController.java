package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.services.UserBookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{id}")
    public ResponseEntity<UserBookResponseDto> createUserBook(@PathVariable Long id, @Valid @RequestBody UserBookRequestDto userBookRequestDto) {
        UserBookResponseDto userBookResponseDto = userBookService.addBookToUserLibrary(id,userBookRequestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userBookResponseDto.bookId()).toUri();
        return ResponseEntity.created(uri).body(userBookResponseDto);
    }
}
