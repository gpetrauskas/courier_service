package gytis.courier.adapter.in.rest.delivery.dto;

import gytis.courier.adapter.out.persistence.delivery.projection.DeliveryOptionProjection;

import java.math.BigDecimal;

public record DeliveryOptionAdminResponse(
        Long id,
        boolean disabled,
        String name,
        String description,
        BigDecimal price
) {
    public static DeliveryOptionAdminResponse toResponse(DeliveryOptionProjection projection) {
        return new DeliveryOptionAdminResponse(
                projection.getId(),
                projection.isDisabled(),
                projection.getName(),
                projection.getDescription(),
                projection.getPrice()
        );
    }
}
