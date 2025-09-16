package com.estudo.bookie.controllers.ExceptionHandler;

import com.estudo.bookie.entities.dtos.exception.ErrorDto;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFound ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorDto errorDto = new ErrorDto(Instant.now(),ex.getMessage(),status.value(),request.getRequestURI());
        return ResponseEntity.status(status).body(errorDto);
    }

}
