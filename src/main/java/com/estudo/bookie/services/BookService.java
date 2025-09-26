package com.estudo.bookie.services;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.BookStatus;
import com.estudo.bookie.entities.UserBook;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.repositories.UserBookRepository;

import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.BookAuthorMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.estudo.bookie.services.specifications.BookSpecifications.*;


@Service
public class BookService {
    private UserBookRepository userBookRepository;
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, UserBookRepository userBookRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.userBookRepository = userBookRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookRequestDto> findAll(String title, String author, Double rating, Pageable pageable) {
        Specification<Book> spec =
                hasKeyword(title)
                        .and(hasAuthor(author))
                        .and(minRating(rating));
        Page<Book> books = bookRepository.findAll(spec, pageable);
        return books.map(BookAuthorMapper.INSTANCE::bookToBookRequestDto);
    }

    @Transactional(readOnly = true)
    public BookRequestDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Book not found"));
        return BookAuthorMapper.INSTANCE.bookToBookRequestDto(book);
    }

    @Transactional
    public BookRequestDto save(BookRequestDto bookRequestDto) {
      Book book = BookAuthorMapper.INSTANCE.bookRequestDtoToBook(bookRequestDto);
      Author author = authorRepository.getReferenceById(bookRequestDto.authorId());
      book.setAuthor(author);
      book = bookRepository.save(book);
      return BookAuthorMapper.INSTANCE.bookToBookRequestDto(book);
    }

    @Transactional
    public BookRequestDto update(Long id, BookRequestDto bookRequestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Book not found"));
        book.setTitle(bookRequestDto.title());
        book = bookRepository.save(book);
        return BookAuthorMapper.INSTANCE.bookToBookRequestDto(book);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFound("Book not found");
        }
            bookRepository.deleteById(id);
    }

    @Transactional
    public Page<BookRequestDto> recommendBooks(Long userId, Pageable pageable) {
        List<UserBook> readBooks = userBookRepository.findByUserIdAndStatus(userId, BookStatus.READ);
        List<Long> readBooksIds = readBooks.stream().map(userbook -> userbook.getBook().getId()).toList();
        List<String> genres = readBooks.stream().map(book -> book.getBook().getGenre()).toList();
        List<String> authors = readBooks.stream().map(book -> book.getBook().getAuthor().getName()).toList();

        Specification<Book> spec = recommendedForUser(readBooksIds, authors, genres);

        Page<Book> books = bookRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("rating").descending()));

        return books.map(BookAuthorMapper.INSTANCE::bookToBookRequestDto);
    }
}
