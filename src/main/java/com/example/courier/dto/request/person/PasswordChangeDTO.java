package com.example.courier.dto.request.person;

import com.example.courier.validation.shared.NotNullOrEmpty;

public record PasswordChangeDTO(
        @NotNullOrEmpty String newPassword,
        @NotNullOrEmpty String currentPassword
) {
}
