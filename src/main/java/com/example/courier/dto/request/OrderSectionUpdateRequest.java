package com.example.courier.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderSectionUpdateRequest(
        @NotNull Long id,
        @NotBlank String sectionToEdit,
        String status,
        String deliveryPreferences
) implements BaseOrderUpdateRequest {
}
