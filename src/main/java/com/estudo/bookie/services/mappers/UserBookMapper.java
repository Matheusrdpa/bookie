package com.estudo.bookie.services.mappers;

import com.estudo.bookie.entities.UserBook;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserBookMapper {
    UserBookMapper INSTANCE = Mappers.getMapper(UserBookMapper.class);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "description", source = "book.description")
    UserBookResponseDto toUserBookResponseDto(UserBook userBook);
    UserBook toUserBook(UserBookResponseDto userBookResponseDto);
}
