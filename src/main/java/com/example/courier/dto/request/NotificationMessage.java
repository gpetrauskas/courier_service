package com.example.courier.dto.request;

import com.example.courier.validation.shared.NotNullOrEmpty;

public record NotificationMessage(@NotNullOrEmpty String title, @NotNullOrEmpty String message) {
}
