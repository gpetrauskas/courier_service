package com.example.courier.dto.request;

import com.example.courier.validation.shared.NotEmptyField;

public record NotificationMessage(@NotEmptyField String title, @NotEmptyField String message) {
}
