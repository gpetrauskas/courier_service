package gytis.courier.adapter.in.rest.delivery.dto;

import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@AtLeastOneField
public record UpdateDeliveryMethodRequest(
        String name,
        String description,
        @Positive BigDecimal price
) {
}
