package com.estudo.bookie.controllers;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/author")
public class AuthorController {

    private final AuthorService authorService;
    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorRequestDto>> findAll() {
       List<AuthorRequestDto> authors = authorService.findAll();
       return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorRequestDto> findById(@PathVariable Long id) {
        AuthorRequestDto author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    @PostMapping
    public ResponseEntity<AuthorRequestDto> save(@RequestBody AuthorRequestDto author) {
        AuthorRequestDto authorSaved = authorService.save(author);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(authorSaved.id()).toUri();
        return ResponseEntity.created(uri).body(authorSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorRequestDto> update(@PathVariable Long id,@RequestBody AuthorRequestDto author) {
        AuthorRequestDto authorUpdated = authorService.update(id, author);
        return ResponseEntity.ok(authorUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
