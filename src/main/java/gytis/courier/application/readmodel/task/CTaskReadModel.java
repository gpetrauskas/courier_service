package gytis.courier.application.readmodel.task;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public record CTaskReadModel(
        Long taskId,
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        List<CTaskItemReadModel> items
) {
}
