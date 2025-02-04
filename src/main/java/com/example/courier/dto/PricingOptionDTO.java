package com.example.courier.dto;

import com.example.courier.domain.PricingOption;

import java.math.BigDecimal;

public record PricingOptionDTO(Long id, String name, String description, BigDecimal price) {
    public static PricingOptionDTO fromPricingOption(PricingOption pricingOption) {
        return new PricingOptionDTO(
                pricingOption.getId(),
                pricingOption.getName(),
                pricingOption.getDescription(),
                pricingOption.getPrice()
        );
    }
}
