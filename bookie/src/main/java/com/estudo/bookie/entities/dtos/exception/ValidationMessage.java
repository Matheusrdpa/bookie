package com.estudo.bookie.entities.dtos.exception;

public class ValidationMessage {
    private String validationField;
    private String ValidationMessage;

    public ValidationMessage(String validationField, String validationMessage) {
        this.validationField = validationField;
        ValidationMessage = validationMessage;
    }

    public String getValidationField() {
        return validationField;
    }

    public void setValidationField(String validationField) {
        this.validationField = validationField;
    }

    public String getValidationMessage() {
        return ValidationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        ValidationMessage = validationMessage;
    }
}
