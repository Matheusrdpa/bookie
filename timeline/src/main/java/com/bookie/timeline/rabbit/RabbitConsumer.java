package com.bookie.timeline.rabbit;

import com.bookie.timeline.entities.BookRequestDto;
import com.bookie.timeline.entities.BookRequestRecord;
import com.bookie.timeline.services.BookRequestService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class RabbitConsumer {

    private final BookRequestService bookRequestService;
    private final ObjectMapper objectMapper;
    public RabbitConsumer(ObjectMapper objectMapper,BookRequestService bookRequestService){
        this.objectMapper = objectMapper;
        this.bookRequestService = bookRequestService;
    }

    @RabbitListener(queues = "book.created")
    public void listen(String message){
        BookRequestRecord rec = objectMapper.readValue(message, BookRequestRecord.class);
        bookRequestService.save(rec);
    }
}
