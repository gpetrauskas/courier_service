package gytis.courier.adapter.in.rest.task.dto;

import gytis.courier.domain.order.ParcelStatus;

public record UpdateItemStatusRequest(
        ParcelStatus status
) {
}
