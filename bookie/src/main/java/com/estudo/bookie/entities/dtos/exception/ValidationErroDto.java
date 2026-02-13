package com.estudo.bookie.entities.dtos.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationErroDto {
    private Instant timeStamp;
    private Integer status;
    private String error;
    private String path;
    private List<ValidationMessage> errors = new ArrayList<>();

    public ValidationErroDto(Instant timeStamp, Integer status, String error, String path) {
        this.timeStamp = timeStamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public void addError(String field, String message) {
        this.errors.add(new ValidationMessage(field, message));
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public List<ValidationMessage> getErrors() {
        return errors;
    }
}
