package com.estudo.bookie.services.exceptions;

public class DuplicateResource extends RuntimeException {
  public DuplicateResource(String message) {
    super(message);
  }
}
