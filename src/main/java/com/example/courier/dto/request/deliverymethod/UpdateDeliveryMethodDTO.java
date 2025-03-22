package com.example.courier.dto.request.deliverymethod;

import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@AtLeastOneField(ignoredFields = "id")
public record UpdateDeliveryMethodDTO(
        @NotNull Long id,
        String name,
        String description,
        @Positive BigDecimal price
        ) {
}
