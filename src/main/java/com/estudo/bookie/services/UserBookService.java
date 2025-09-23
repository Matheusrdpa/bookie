package com.estudo.bookie.services;

import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.UserBook;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.repositories.UserBookRepository;
import com.estudo.bookie.repositories.UserRepository;
import com.estudo.bookie.services.exceptions.DuplicateResource;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.UserBookMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("Book can only be added to your library"));
        Book book = bookRepository.findById(userBookRequestDto.bookId()).orElseThrow(() -> new ResourceNotFound("Book Not Found"));

        if (userBookRepository.findByUserIdAndBookId(userId, book.getId()).isPresent()) {
            throw new DuplicateResource("This book already exists in user library");
        }

        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setRating(userBookRequestDto.rating());
        userBookRepository.save(userBook);
        return UserBookMapper.INSTANCE.toUserBookResponseDto(userBook);
    }

    @Transactional
    public void deleteBookFromUserLibrary(Long userId, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() -> new ResourceNotFound("Book Not Found"));
        userBookRepository.delete(userBook);
    }

    @Transactional(readOnly = true)
    public Page<UserBookResponseDto> getUserLibrary(String username, Pageable pageable) {
        Page<UserBook> page = userBookRepository.findByUserUsername(username,pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFound("No user library found");
        }
       return page.map(UserBookMapper.INSTANCE::toUserBookResponseDto);
    }

    @Transactional
    public UserBookResponseDto updateUserBook(Long userId, Long bookId, Double rating) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() -> new ResourceNotFound("Book or User Not Found"));
        userBook.setRating(rating);
        userBookRepository.save(userBook);
        return UserBookMapper.INSTANCE.toUserBookResponseDto(userBook);
    }

    @Transactional
    public void removeUserFromUserLibrary(Long userId, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() -> new ResourceNotFound("Book or User Not Found"));
        userBookRepository.delete(userBook);
    }
}
