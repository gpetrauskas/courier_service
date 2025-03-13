package com.example.courier.dto.response.deliveryoption;

import java.math.BigDecimal;

public record DeliveryOptionUserResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price
) implements DeliveryOptionDTO {
}
