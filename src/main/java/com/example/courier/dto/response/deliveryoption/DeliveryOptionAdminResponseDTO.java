package com.example.courier.dto.response.deliveryoption;

import java.math.BigDecimal;

public record DeliveryOptionAdminResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean disabled
) implements DeliveryOptionDTO {
}
