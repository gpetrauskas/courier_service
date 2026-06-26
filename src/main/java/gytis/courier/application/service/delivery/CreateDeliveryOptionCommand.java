package gytis.courier.application.service.delivery;

import java.math.BigDecimal;

public record CreateDeliveryOptionCommand(
        String name,
        String description,
        BigDecimal price,
        boolean disabled
) {
}
