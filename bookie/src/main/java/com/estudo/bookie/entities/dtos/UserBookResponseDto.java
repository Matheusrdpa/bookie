package com.estudo.bookie.entities.dtos;

import com.estudo.bookie.entities.BookStatus;

public record UserBookResponseDto(Long bookId, String bookTitle, Double rating, String description, BookStatus status) {
}
