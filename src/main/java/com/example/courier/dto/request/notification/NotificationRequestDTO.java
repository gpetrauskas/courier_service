package com.example.courier.dto.request.notification;

import com.example.courier.service.notification.NotificationTarget;

public record NotificationRequestDTO(
        String title,
        String message,
        NotificationTarget type
) {
}
