package gytis.courier.application.service.delivery;

import java.math.BigDecimal;

public record UpdateDeliveryOptionCommand(
        Long id,
        String name,
        String description,
        BigDecimal price
) {
}
