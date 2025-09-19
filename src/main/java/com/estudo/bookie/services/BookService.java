package com.estudo.bookie.services;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.repositories.BookRepository;
import com.estudo.bookie.services.exceptions.DataIntegrityException;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.BookAuthorMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class BookService {
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<BookRequestDto> findAll() {
        List<Book> books = bookRepository.findAll();
        List<BookRequestDto> bookRequestDtos = books.stream().map(BookAuthorMapper.INSTANCE::bookToBookRequestDto).toList();
        return bookRequestDtos;
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
}
