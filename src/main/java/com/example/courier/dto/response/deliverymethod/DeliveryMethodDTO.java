package com.example.courier.dto.response.deliverymethod;

import java.math.BigDecimal;

public sealed interface DeliveryMethodDTO permits DeliveryMethodAdminResponseDTO, DeliveryMethodUserResponseDTO {
    String name();
    String description();
    BigDecimal price();
}
