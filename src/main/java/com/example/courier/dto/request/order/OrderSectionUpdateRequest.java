package com.example.courier.dto.request.order;

import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@AtLeastOneField
public record OrderSectionUpdateRequest(
        @NotNull Long id,
        @NotBlank String sectionToEdit,
        String status,
        String deliveryPreferences
) implements BaseOrderUpdateRequest {
}
