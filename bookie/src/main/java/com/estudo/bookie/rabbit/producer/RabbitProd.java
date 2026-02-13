package com.estudo.bookie.rabbit.producer;

import com.estudo.bookie.entities.Book;
import com.estudo.bookie.entities.dtos.BookRequestDto;
import com.estudo.bookie.entities.dtos.UserBookRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.estudo.bookie.rabbit.Rabbitconfig;

@Service
public class RabbitProd {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    public RabbitProd(RabbitTemplate rabbitTemplate,ObjectMapper objectMapper){
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(BookRequestDto book) {
        try {
            String message = objectMapper.writeValueAsString(book);
            rabbitTemplate.convertAndSend(Rabbitconfig.QUEUE_NAME,message);
        }catch (JsonProcessingException e){
            e.getMessage();
        }
    }
}
