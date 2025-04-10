package com.example.courier.dto.request.notification;

import com.example.courier.common.NotificationTargetType;

public record NotificationRequestDTO(String title, String message, NotificationTargetType type) {
}
