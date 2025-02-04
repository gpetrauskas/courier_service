package com.example.courier.dto;

import java.time.LocalDateTime;

public record ApiResponseDTO(String status, String message, LocalDateTime timestamp) {
    public ApiResponseDTO(String status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
