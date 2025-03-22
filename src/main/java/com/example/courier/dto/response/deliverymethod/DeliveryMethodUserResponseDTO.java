package com.example.courier.dto.response.deliverymethod;

import java.math.BigDecimal;

public record DeliveryMethodUserResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price
) implements DeliveryMethodDTO {
}
