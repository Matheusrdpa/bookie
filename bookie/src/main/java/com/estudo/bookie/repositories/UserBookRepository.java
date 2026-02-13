package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.BookStatus;
import com.estudo.bookie.entities.UserBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);
    Page<UserBook> findByUserUsername(String username, Pageable pageable);
    List<UserBook> findByUserIdAndStatus(Long userId, BookStatus status);
}
