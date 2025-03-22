package com.example.courier.dto.response.deliverymethod;

import java.math.BigDecimal;

public record DeliveryMethodAdminResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean disabled
) implements DeliveryMethodDTO {
}
