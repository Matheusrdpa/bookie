package com.estudo.bookie.entities.dtos;

import jakarta.validation.constraints.NotNull;

public record UserBookRequestDto(@NotNull(message = "Please pick a book")Long bookId, Double rating) {
}
