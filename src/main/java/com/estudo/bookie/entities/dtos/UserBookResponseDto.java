package com.estudo.bookie.entities.dtos;

public record UserBookResponseDto(Long bookId, String bookTitle, Double rating,String description) {
}
