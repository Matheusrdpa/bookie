package com.bookie.timeline.services;

import com.bookie.timeline.entities.BookRequestDto;
import com.bookie.timeline.entities.BookRequestRecord;
import com.bookie.timeline.repositories.BookRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookRequestService {
    private BookRequestRepository bookRequestRepository;
    public BookRequestService(BookRequestRepository bookRequestRepository){
        this.bookRequestRepository = bookRequestRepository;
    }

    public List<BookRequestDto> findAll(){
       return bookRequestRepository.findAll();
    }

    public BookRequestDto save(BookRequestRecord rec){
        BookRequestDto dto = new BookRequestDto();
        dto.setBookieId(rec.id());
        dto.setTitle(rec.title());
        dto.setAuthorId(rec.authorId());
        dto.setGenre(rec.genre());
        dto.setRating(rec.rating());
        dto.setDescriptio(rec.description());
        dto.setLocalDateTime(LocalDateTime.now());
        return bookRequestRepository.save(dto);
    }
}
