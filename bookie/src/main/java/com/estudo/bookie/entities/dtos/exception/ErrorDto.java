package com.estudo.bookie.entities.dtos.exception;

import java.time.Instant;

public record ErrorDto(Instant timestamp, String error, Integer status, String path) {
}
