package com.example.courier.dto.request.deliveryoption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateDeliveryOptionDTO(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive BigDecimal price) {
}
