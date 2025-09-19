package com.estudo.bookie.entities.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserBookRequestDto(@NotBlank(message = "Please pick a book")Long bookId, Double rating) {
}
