package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
