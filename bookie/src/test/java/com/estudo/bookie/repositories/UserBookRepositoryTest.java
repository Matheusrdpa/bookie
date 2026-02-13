package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.BookStatus;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.UserBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserBookRepositoryTest {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("mail@test");
        user.setUsername("reader");
        user.setPassword("123");
        user = userRepository.save(user);

        Author author = new Author(null, "test", "Bio", null);
        author = authorRepository.save(author);

        book = new Book(null, "test book", author, 5.0, "Genre", "Desc");
        book = bookRepository.save(book);

        UserBook userBook = new UserBook(book, user, 4.5, BookStatus.READING);
        userBookRepository.save(userBook);
    }

    @Test
    @DisplayName("Should find userbook by user id and book id")
    void findByUserIdAndBookId_Should_Return_UserBook() {
        Optional<UserBook> result = userBookRepository.findByUserIdAndBookId(user.getId(), book.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(BookStatus.READING, result.get().getStatus());
    }

    @Test
    @DisplayName("Should return empty if combination doesn't exist")
    void findByUserIdAndBookId_Should_Return_Empty() {
        Long fakeBookId = 999L;
        Optional<UserBook> result = userBookRepository.findByUserIdAndBookId(user.getId(), fakeBookId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find book page by username")
    void findByUserUsername_Should_Return_Page() {
        Page<UserBook> result = userBookRepository.findByUserUsername(
                "reader",
                PageRequest.of(0, 10)
        );

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("test book", result.getContent().get(0).getBook().getTitle());
    }

    @Test
    @DisplayName("Should list book by status and user id")
    void findByUserIdAndStatus_Should_Return_List() {
        List<UserBook> readingList = userBookRepository.findByUserIdAndStatus(user.getId(), BookStatus.READING);
        List<UserBook> finishedList = userBookRepository.findByUserIdAndStatus(user.getId(), BookStatus.READ);

        Assertions.assertEquals(1, readingList.size());
        Assertions.assertTrue(finishedList.isEmpty());
    }
}