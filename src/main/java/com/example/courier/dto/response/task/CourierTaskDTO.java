package com.example.courier.dto.response.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.dto.CourierTaskItemDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CourierTaskDTO(
        Long courierId,
        Long taskId,
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        List<CourierTaskItemDTO> taskItemDTOS
) implements TaskBase {
}
