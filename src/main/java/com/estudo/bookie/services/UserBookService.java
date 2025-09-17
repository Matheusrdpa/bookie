package com.estudo.bookie.services;

import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.UserBook;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.repositories.UserBookRepository;
import com.estudo.bookie.repositories.UserRepository;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.UserBookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBookService {
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private UserBookRepository userBookRepository;

    public UserBookService(UserRepository userRepository, BookRepository bookRepository, UserBookRepository userBookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userBookRepository = userBookRepository;
    }

    @Transactional
    public UserBookResponseDto addBookToUserLibrary(Long userId, UserBookRequestDto userBookRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("User Not Found"));
        Book book = bookRepository.findById(userBookRequestDto.bookId()).orElseThrow(() -> new ResourceNotFound("Book Not Found"));

        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setRating(userBookRequestDto.rating());
        userBookRepository.save(userBook);
        return UserBookMapper.INSTANCE.toUserBookResponseDto(userBook);
    }
}
