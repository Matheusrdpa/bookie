package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.services.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/v1/book")
public class BookController {
    private BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookRequestDto>> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Double rating,
            Pageable pageable) {
        return ResponseEntity.ok(bookService.findAll(title,author, rating,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookRequestDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookRequestDto> save(@Valid @RequestBody BookRequestDto bookRequestDto) {
      BookRequestDto book = bookService.save(bookRequestDto);
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(book.id()).toUri();
      return ResponseEntity.created(uri).body(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookRequestDto> update(@PathVariable Long id,@Valid @RequestBody BookRequestDto bookRequestDto) {
        return ResponseEntity.ok(bookService.update(id, bookRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookRequestDto> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recommended")
    public Page<BookRequestDto> getRecommendedBooks(
            @AuthenticationPrincipal CustomUserDetails loggedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.recommendBooks(loggedUser.getId(), pageable);
    }
}
