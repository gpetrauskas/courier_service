package gytis.courier.adapter.in.rest.security.dto;

import gytis.courier.adapter.in.rest.common.validation.NotNullOrEmpty;

public record PasswordChangeRequest(
        @NotNullOrEmpty String newPassword,
        @NotNullOrEmpty String currentPassword
) {
}
