package com.example.courier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String address,
        @NotBlank @Size(min = 8, max = 16, message = "Password must be between 8-16 characters.") String password
) {
}
