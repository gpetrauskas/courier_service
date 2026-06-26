package gytis.courier.application.service.task;

import gytis.courier.domain.order.ParcelStatus;

public record UpdateItemStatusCommand(
        Long myId,
        Long taskId,
        Long taskItemId,
        ParcelStatus status
) {
}
