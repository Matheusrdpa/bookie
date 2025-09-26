package com.estudo.bookie.entities.dtos;

import com.estudo.bookie.entities.BookStatus;
import jakarta.validation.constraints.NotNull;

public record UserBookRequestDto(@NotNull(message = "Please pick a book")Long bookId, Double rating, BookStatus status) {
}
