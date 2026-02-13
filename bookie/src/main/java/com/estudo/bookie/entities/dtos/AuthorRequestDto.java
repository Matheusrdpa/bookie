package com.estudo.bookie.entities.dtos;

import java.util.List;

public record AuthorRequestDto(Long id, String name, String bio, List<BookRequestDto> books) {
}
