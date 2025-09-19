package com.estudo.bookie.entities.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public record UserRequestDto(
        Long id,
        @NotBlank(message = "Username can't be empty")
        String username,
        @Email @NotBlank(message = "Email can't be empty")
        String email,
        @NotBlank(message = "Password can't be empty")
        String password
) {
}
