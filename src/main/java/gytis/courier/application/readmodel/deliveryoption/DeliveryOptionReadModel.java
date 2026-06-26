package gytis.courier.application.readmodel.deliveryoption;

import java.math.BigDecimal;

public record DeliveryOptionReadModel(
        Long id,
        boolean disabled,
        String name,
        String description,
        BigDecimal price
) {
}
