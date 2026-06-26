package gytis.courier.application.readmodel.task;

import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public record AdminTaskReadModel(
        Long id,
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        Long courierId,
        String courierName,
        String courierPhoneNumber,
        List<AdminTaskItemReadModel> items
) {
}
