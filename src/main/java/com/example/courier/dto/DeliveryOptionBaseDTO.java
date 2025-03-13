package com.example.courier.dto;

import com.example.courier.domain.DeliveryOption;

import java.math.BigDecimal;

public record DeliveryOptionBaseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price
) {
    public static DeliveryOptionBaseDTO fromDeliveryOption(DeliveryOption deliveryOption) {
        return new DeliveryOptionBaseDTO(
                deliveryOption.getId(),
                deliveryOption.getName(),
                deliveryOption.getDescription(),
                deliveryOption.getPrice()
        );
    }
}
