package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
