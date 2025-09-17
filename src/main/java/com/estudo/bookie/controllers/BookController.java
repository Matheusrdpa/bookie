package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.services.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/book")
public class BookController {
    private BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookRequestDto>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
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
}
