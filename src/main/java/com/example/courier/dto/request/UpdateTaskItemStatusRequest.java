package com.example.courier.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskItemStatusRequest(
        @NotBlank String status
) {
}
