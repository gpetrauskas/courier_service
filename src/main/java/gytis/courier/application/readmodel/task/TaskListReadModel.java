package gytis.courier.application.readmodel.task;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

import java.time.LocalDateTime;

public record TaskListReadModel(
        Long id,
        Long courierId,
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
