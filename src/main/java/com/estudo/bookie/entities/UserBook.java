package com.estudo.bookie.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Table(name = "tb_user_books")
@Entity
public class UserBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @DecimalMax(value = "5.0", inclusive = true, message = "Maximum rating is 5")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum rating is 5")
    private Double rating;

    public UserBook() {}

    public UserBook(Book book, User user, Double rating) {
        this.book = book;
        this.user = user;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
