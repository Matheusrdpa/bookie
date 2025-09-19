package com.estudo.bookie.controllers.ExceptionHandler;

import com.estudo.bookie.entities.dtos.exception.ErrorDto;
import com.estudo.bookie.services.exceptions.DataIntegrityException;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        ErrorDto errorDto = new ErrorDto(Instant.now(),errorMessage,status.value(),request.getRequestURI());
        return ResponseEntity.status(status).body(errorDto);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrity(DataIntegrityException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDto errorDto = new ErrorDto(Instant.now(),ex.getMessage(),status.value(),request.getRequestURI());
        return ResponseEntity.status(status).body(errorDto);
    }

}
