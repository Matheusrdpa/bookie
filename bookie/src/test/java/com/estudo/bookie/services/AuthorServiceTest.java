package com.estudo.bookie.services;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.services.exceptions.DataIntegrityException;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    private Author author;
    private AuthorRequestDto authorDto;
    private BookRequestDto bookRequestDto;
    private Long existingId = 1L;
    private Long nonExistingId = 100L;
    private Long dependentId = 2L;

    @BeforeEach
    void setUp() {
        author = new Author(existingId, "George R. R. Martin", "Autor de Game of Thrones", null);
        bookRequestDto = new BookRequestDto(existingId,"A Song of ice and fire",1L, 3.5,"Fantasy","desc");
        authorDto = new AuthorRequestDto(existingId, "George R. R. Martin", "Bio",List.of(bookRequestDto));
    }

    @Test
    void findById_Should_Return_AuthorDto_When_Id_Exists() {
        when(authorRepository.findById(existingId)).thenReturn(Optional.of(author));

        var result = authorService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("George R. R. Martin", result.name());
        verify(authorRepository).findById(existingId);
    }

    @Test
    void findById_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(authorRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFound.class, () -> authorService.findById(nonExistingId));
        verify(authorRepository).findById(nonExistingId);
    }

    @Test
    void save_Should_Return_AuthorDto() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        var result = authorService.save(authorDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(author.getName(), result.name());
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void update_Should_Return_UpdatedDto_When_Id_Exists() {
        when(authorRepository.findById(existingId)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorRequestDto result = authorService.update(existingId, authorDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(authorDto.name(), result.name());
        verify(authorRepository).findById(existingId);
    }

    @Test
    void update_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(authorRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFound.class, () -> authorService.update(nonExistingId, authorDto));
        verify(authorRepository, Mockito.never()).save(any());
    }

    @Test
    void delete_Should_Do_Nothing_When_Id_Exists() {
        when(authorRepository.existsById(existingId)).thenReturn(true);

        Mockito.doNothing().when(authorRepository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> authorService.delete(existingId));
        verify(authorRepository).deleteById(existingId);
    }

    @Test
    void delete_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(authorRepository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFound.class, () -> authorService.delete(nonExistingId));
        verify(authorRepository, Mockito.never()).deleteById(any());
    }

    @Test
    void delete_Should_Throw_DataIntegrityException_When_Author_Has_Books() {
        when(authorRepository.existsById(dependentId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(authorRepository).deleteById(dependentId);

        Assertions.assertThrows(DataIntegrityException.class, () -> authorService.delete(dependentId));

        verify(authorRepository).deleteById(dependentId);
    }

    @Test
    void findAll_Should_Return_Page_Of_Authors() {
        List<Author> authors = List.of(author);

        when(authorRepository.findAll()).thenReturn(authors);

        List<AuthorRequestDto> result = authorService.findAll();

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("George R. R. Martin", result.getFirst().name());
    }
}