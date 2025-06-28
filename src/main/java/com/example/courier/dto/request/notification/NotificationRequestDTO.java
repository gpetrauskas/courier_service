package com.example.courier.dto.request.notification;

import com.example.courier.service.notification.NotificationTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequestDTO(
        @NotBlank String title,
        @NotBlank String message,
        @NotNull NotificationTarget type
) {
}
