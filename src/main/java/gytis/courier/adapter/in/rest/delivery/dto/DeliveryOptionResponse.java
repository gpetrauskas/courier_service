package gytis.courier.adapter.in.rest.delivery.dto;

import gytis.courier.adapter.out.persistence.delivery.projection.DeliveryOptionProjection;

import java.math.BigDecimal;

public record DeliveryOptionResponse(
        String name,
        String description,
        BigDecimal price
) {
    public static DeliveryOptionResponse toResponse(DeliveryOptionProjection projection) {
        return new DeliveryOptionResponse(
                projection.getName(),
                projection.getDescription(),
                projection.getPrice()
        );
    }
}
