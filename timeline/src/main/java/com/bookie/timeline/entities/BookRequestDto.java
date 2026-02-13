package com.bookie.timeline.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_book")
public class BookRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long bookieId;
    private String title;
    private Long authorId;
    private Double rating;
    private String genre;
    private String descriptio;
    private LocalDateTime localDateTime;

    public BookRequestDto() {
    }

    public BookRequestDto(Long id,Long bookieId, String title, Long authorId, Double rating, String genre, String descriptio,LocalDateTime localDateTime) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.rating = rating;
        this.genre = genre;
        this.descriptio = descriptio;
        this.bookieId = bookieId;
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Long getBookieId() {
        return bookieId;
    }

    public void setBookieId(Long bookieId) {
        this.bookieId = bookieId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescriptio() {
        return descriptio;
    }

    public void setDescriptio(String descriptio) {
        this.descriptio = descriptio;
    }
}
