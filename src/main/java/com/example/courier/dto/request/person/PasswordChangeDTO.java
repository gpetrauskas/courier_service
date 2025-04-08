package com.example.courier.dto.request.person;

import com.example.courier.validation.shared.NotEmptyField;

public record PasswordChangeDTO(
        @NotEmptyField String newPassword,
        @NotEmptyField String currentPassword
) {
}
