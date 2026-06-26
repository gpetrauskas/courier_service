package gytis.courier.adapter.in.rest.delivery.dto;

import gytis.courier.domain.delivery.DeliveryGroup;

import java.util.List;
import java.util.Map;

public record DeliveryOptionsResponse(
        Map<DeliveryGroup, List<DeliveryOptionResponse>> grouped
) {
}
