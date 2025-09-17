package com.estudo.bookie.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;

@Entity
@Table(name = "tb_books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double rating;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    public Book(Long id, String title, Author author, Double rating, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Book(){}

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
