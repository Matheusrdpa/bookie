package com.estudo.bookie.services;

import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.BookStatus;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.UserBook;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.repositories.UserBookRepository;
import com.estudo.bookie.repositories.UserRepository;
import com.estudo.bookie.services.exceptions.DuplicateResource;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserBookServiceTest {

    @InjectMocks
    private UserBookService userBookService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserBookRepository userBookRepository;

    private User user;
    private Book book;
    private UserBook userBook;
    private UserBookRequestDto userBookRequestDto;
    private Long userId = 1L;
    private Long bookId = 10L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("User");

        book = new Book();
        book.setId(bookId);
        book.setTitle("Name of the wind");

        userBook = new UserBook(book, user, 5.0, BookStatus.READING);

        userBookRequestDto = new UserBookRequestDto(bookId, 5.0, BookStatus.READING);
    }

    @Test
    void addBook_Should_Save_When_Everything_Is_Ok() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userBookRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);

        UserBookResponseDto result = userBookService.addBookToUserLibrary(userId, userBookRequestDto);

        Assertions.assertNotNull(result);
        verify(userBookRepository).save(any(UserBook.class));
    }

    @Test
    void addBook_Should_Throw_DuplicateResource_When_Book_Already_In_Library() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userBookRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(userBook));

        Assertions.assertThrows(DuplicateResource.class, () -> {
            userBookService.addBookToUserLibrary(userId, userBookRequestDto);
        });

        verify(userBookRepository, Mockito.never()).save(any(UserBook.class));
    }

    @Test
    void getUserLibrary_Should_Return_Page_Of_Books() {
        Page<UserBook> page = new PageImpl<>(List.of(userBook));
        Pageable pageable = PageRequest.of(0, 10);

        when(userBookRepository.findByUserUsername("User", pageable)).thenReturn(page);

        var result = userBookService.getUserLibrary("User", pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUserLibrary_Should_Throw_ResourceNotFound_When_Library_Is_Empty() {
        Page<UserBook> emptyPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);

        when(userBookRepository.findByUserUsername("User", pageable)).thenReturn(emptyPage);

        Assertions.assertThrows(ResourceNotFound.class, () -> {
            userBookService.getUserLibrary("User", pageable);
        });
    }

    @Test
    void updateUserBook_Should_Update_Rating() {
        Double newRating = 3.5;

        when(userBookRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(userBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);

        userBookService.updateUserBook(userId, bookId, newRating);

        Assertions.assertEquals(newRating, userBook.getRating());
        verify(userBookRepository).save(userBook);
    }

    @Test
    void deleteUserBook_Should_Delete_When_Exists() {
        when(userBookRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(userBook));

        userBookService.deleteBookFromUserLibrary(userId, bookId);

        verify(userBookRepository).delete(userBook);
    }

    @Test
    void deleteUserBook_Should_Throw_ResourceNotFound_When_Not_Exists() {
        when(userBookRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFound.class, () -> {
            userBookService.deleteBookFromUserLibrary(userId, bookId);
        });

        verify(userBookRepository, Mockito.never()).delete(any());
    }
}