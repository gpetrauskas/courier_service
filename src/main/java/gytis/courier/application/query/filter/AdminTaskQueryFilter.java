package gytis.courier.application.query.filter;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

public record AdminTaskQueryFilter(
        Long courierId,
        Long taskListId,
        TaskType taskType,
        DeliveryStatus deliveryStatus
) {
}
