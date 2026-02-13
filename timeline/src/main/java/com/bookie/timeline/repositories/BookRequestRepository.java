package com.bookie.timeline.repositories;

import com.bookie.timeline.entities.BookRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRequestRepository extends JpaRepository<BookRequestDto,Long> {
}
