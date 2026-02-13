package com.estudo.bookie.services;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.repositories.AuthorRepository;
import com.estudo.bookie.services.exceptions.DataIntegrityException;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.BookAuthorMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AuthorService {
    private AuthorRepository authorRepository;
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public List<AuthorRequestDto> findAll(){
        List<Author> authors = authorRepository.findAll();
        List<AuthorRequestDto> authorRequestDtos = authors.stream().map(BookAuthorMapper.INSTANCE::authorToAuthorRequestDto).toList();
        return authorRequestDtos;
    }

    @Transactional
    public AuthorRequestDto findById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Author not found"));
        return BookAuthorMapper.INSTANCE.authorToAuthorRequestDto(author);
    }

    @Transactional
    public AuthorRequestDto save(AuthorRequestDto authorRequestDto) {
        Author author = BookAuthorMapper.INSTANCE.authorRequestDtoToAuthor(authorRequestDto);
        author = authorRepository.save(author);
        return BookAuthorMapper.INSTANCE.authorToAuthorRequestDto(author);
    }

    @Transactional
    public AuthorRequestDto update(Long id, AuthorRequestDto authorRequestDto) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Author not found"));
        author.setName(authorRequestDto.name());
        author.setBio(authorRequestDto.bio());
        return BookAuthorMapper.INSTANCE.authorToAuthorRequestDto(author);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!authorRepository.existsById(id)) {
            throw new ResourceNotFound("Author not found");
        }
        try {
            authorRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityException("Entity depends on another entity and can't be removed");
        }
    }
}

