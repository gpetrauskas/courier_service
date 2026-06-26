package gytis.courier.adapter.in.rest.order.dto.request;

import gytis.courier.domain.order.OrderStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record OrderAdminSearchRequest(
        @Min(0) @Max(100) int page,
        @Min(1) @Max(100) int size,
        String sortField,
        String direction,
        OrderStatus status,
        Long id,
        Long userId
) {
}
