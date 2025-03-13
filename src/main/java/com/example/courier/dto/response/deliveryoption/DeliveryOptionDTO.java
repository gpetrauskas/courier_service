package com.example.courier.dto.response.deliveryoption;

import java.math.BigDecimal;

public sealed interface DeliveryOptionDTO permits DeliveryOptionAdminResponseDTO, DeliveryOptionUserResponseDTO {
    String name();
    String description();
    BigDecimal price();
}
