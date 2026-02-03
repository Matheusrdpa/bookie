package com.estudo.bookie.services;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    private Book book;
    private Author author;
    private BookRequestDto bookRequestDto;
    private Long existingId = 1L;
    private Long nonExistingId = 100L;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "George R R martin", "Bio", null);
        book = new Book(existingId, "A song of ice and fire", author, 4.5, "Distopia", "Fantasy");
        bookRequestDto = new BookRequestDto(existingId, "A song of ice and fire", 1L, 4.5, "Fantasy", "Fantasy");
    }

    @Test
    void findById_Should_Return_BookDto_When_Id_Exists() {
        when(bookRepository.findById(existingId)).thenReturn(Optional.of(book));

        BookRequestDto result = bookService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("A song of ice and fire", result.title());
        verify(bookRepository).findById(existingId);
    }

    @Test
    void findById_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFound.class, () -> bookService.findById(nonExistingId));
        verify(bookRepository).findById(nonExistingId);
    }

    @Test
    void save_Should_Return_BookDto() {
        when(authorRepository.getReferenceById(1L)).thenReturn(author);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookRequestDto result = bookService.save(bookRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("A song of ice and fire", result.title());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_Should_Return_UpdatedBookDto_When_Id_Exists() {
        when(bookRepository.findById(existingId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookRequestDto result = bookService.update(existingId, bookRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("A song of ice and fire", result.title());
        verify(bookRepository).findById(existingId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFound.class, () -> bookService.update(nonExistingId, bookRequestDto));

        verify(bookRepository, Mockito.never()).save(any(Book.class));
    }

    @Test
    void delete_Should_Do_Nothing_When_Id_Exists() {
        when(bookRepository.existsById(existingId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> bookService.delete(existingId));

        verify(bookRepository).deleteById(existingId);
    }

    @Test
    void delete_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(bookRepository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFound.class, () -> bookService.delete(nonExistingId));

        verify(bookRepository, Mockito.never()).deleteById(any());
    }

    @Test
    void findAll_Should_Return_Page_Of_Books() {

        List<Book> books = List.of(book);
        Page<Book> page = new PageImpl<>(books);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<BookRequestDto> result = bookService.findAll("A song of ice and fire", null, null, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
    }
}