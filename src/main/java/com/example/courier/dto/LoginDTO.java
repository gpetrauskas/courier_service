package com.example.courier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 16) String password) {
}