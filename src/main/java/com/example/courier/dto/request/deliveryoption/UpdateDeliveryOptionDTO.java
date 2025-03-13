package com.example.courier.dto.request.deliveryoption;

import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@AtLeastOneField(ignoredFields = "id")
public record UpdateDeliveryOptionDTO(
        @NotNull Long id,
        String name,
        String description,
        @Positive BigDecimal price
        ) {
}
