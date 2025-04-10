package com.example.courier.dto.response.notification;

import java.time.LocalDateTime;

public record NotificationResponseDTO(Long id, String title, String message, LocalDateTime createdAt, boolean isRead) {
}
