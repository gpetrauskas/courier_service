package com.example.courier.dto;

import com.example.courier.domain.PricingOption;
import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@AtLeastOneField(ignoredFields = "id")
public record PricingOptionDTO(
        @NotNull Long id,
        String name,
        String description,
        @Positive BigDecimal price
) {
    public static PricingOptionDTO fromPricingOption(PricingOption pricingOption) {
        return new PricingOptionDTO(
                pricingOption.getId(),
                pricingOption.getName(),
                pricingOption.getDescription(),
                pricingOption.getPrice()
        );
    }
}
