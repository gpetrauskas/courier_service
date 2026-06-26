package gytis.courier.adapter.in.rest.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record OrderUserSearchRequest(
        @Min(0) @Max(100) int page,
        @Min(1) @Max(100) int size,
        String sortField,
        String direction
) {
}
