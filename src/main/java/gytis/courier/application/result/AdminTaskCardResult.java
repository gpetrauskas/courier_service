package gytis.courier.application.result;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

public record AdminTaskCardResult(
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        Long count
) {
}
