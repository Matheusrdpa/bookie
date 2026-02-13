package com.estudo.bookie.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tb_authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "author")
    List<Book> books;

    public Author(Long id, String name, String bio, List<Book> books) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.books = books;
    }

    public Author() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
