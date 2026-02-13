package com.estudo.bookie.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rabbitconfig {
    public static final String QUEUE_NAME = "book.created";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME,true);
    }
}
