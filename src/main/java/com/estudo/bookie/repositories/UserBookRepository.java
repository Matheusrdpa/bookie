package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
}
