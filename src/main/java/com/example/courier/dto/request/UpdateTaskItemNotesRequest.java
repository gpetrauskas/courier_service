package com.example.courier.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskItemNotesRequest(
        @NotBlank String note,
        @NotNull Long updateBy) {
}
