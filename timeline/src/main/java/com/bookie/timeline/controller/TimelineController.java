package com.bookie.timeline.controller;

import com.bookie.timeline.entities.BookRequestDto;
import com.bookie.timeline.services.BookRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timeline")
public class TimelineController {
    private BookRequestService bookRequestService;
    public TimelineController(BookRequestService bookRequestService){
        this.bookRequestService = bookRequestService;
    }

    @GetMapping
    public ResponseEntity<List<BookRequestDto>> getActivities(){
      List<BookRequestDto> list = bookRequestService.findAll();
      return ResponseEntity.ok(list);
    }
}
