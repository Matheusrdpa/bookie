package com.estudo.bookie.services.mappers;

import com.estudo.bookie.entities.Author;
import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.dtos.AuthorRequestDto;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookAuthorMapper {

    BookAuthorMapper INSTANCE = Mappers.getMapper(BookAuthorMapper.class);

    AuthorRequestDto authorToAuthorRequestDto(Author author);
    Author authorRequestDtoToAuthor(AuthorRequestDto authorRequestDto);

    @Mapping(source = "author.id", target = "authorId")
    BookRequestDto bookToBookRequestDto(Book book);
    Book bookRequestDtoToBook(BookRequestDto bookRequestDto);
}
