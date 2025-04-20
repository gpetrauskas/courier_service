package com.example.courier.dto.response.notification;

import com.example.courier.dto.NotificationBase;
import com.example.courier.service.notification.NotificationTarget;

import java.time.LocalDateTime;

public record AdminNotificationResponseDTO(
        Long id,
        String title,
        String message,
        LocalDateTime createdAt,
        NotificationTarget target
) implements NotificationBase {
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}