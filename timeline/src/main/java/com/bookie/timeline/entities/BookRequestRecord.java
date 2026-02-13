package com.bookie.timeline.entities;

public record BookRequestRecord(
        Long id,
        String title,
        Long authorId,
        Double rating,
        String genre,
        String description
) {
}