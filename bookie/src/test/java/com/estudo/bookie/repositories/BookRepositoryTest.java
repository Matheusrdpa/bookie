package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.estudo.bookie.services.specifications.BookSpecifications.hasAuthor;
import static com.estudo.bookie.services.specifications.BookSpecifications.hasKeyword;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        Author author1 = new Author(null, "J.R.R. Tolkien", "Bio", null);
        Author author2 = new Author(null, "George Orwell", "Bio", null);

        authorRepository.saveAll(List.of(author1, author2));

        Book book1 = new Book(null, "Lord of the rings", author1, 5.0, "Fantasy", "Desc");
        Book book2 = new Book(null, "The Hobbit", author1, 4.8, "Fantasy", "Desc");
        Book book3 = new Book(null, "1984", author2, 4.5, "Distopia", "Desc");

        bookRepository.saveAll(List.of(book1, book2, book3));
    }

    @Test
    @DisplayName("Should return books that has keyword in title")
    void findAll_Should_Filter_By_Keyword() {
        var spec = hasKeyword("rings");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findAll(spec, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Lord of the rings", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Should return books filtered by author name (Case Insensitive)")
    void findAll_Should_Filter_By_Author() {
        var spec = hasAuthor("tolkien");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findAll(spec, pageable);

        Assertions.assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Should work combining filters")
    void findAll_Should_Filter_By_Keyword_AND_Author() {
        var spec = hasAuthor("Tolkien").and(hasKeyword("Hobbit"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findAll(spec, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("The Hobbit", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Should return empty when no match")
    void findAll_Should_Return_Empty_When_No_Match() {
        var spec = hasAuthor("Tolkien").and(hasKeyword("Harry Potter"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findAll(spec, pageable);

        Assertions.assertTrue(result.isEmpty());
    }
}