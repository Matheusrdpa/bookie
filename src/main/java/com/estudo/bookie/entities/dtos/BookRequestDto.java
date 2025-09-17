package com.estudo.bookie.entities.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record BookRequestDto(
        Long id,
        String title,
        Long authorId,
        @DecimalMax(value = "5.0", inclusive = true, message = "Max rating allowed is 5.0")
        @DecimalMin(value = "0.0", inclusive = true, message = "Min rating allowed is 0.0")
        Double rating,
        String description
) {
}
