package com.example.courier.dto;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.DeliveryTask;

import java.time.LocalDateTime;
import java.util.List;

public record DeliveryTaskDTO(
        Long taskId,
        CourierDTO courierDTO,
        Long adminId,
        List<DeliveryTaskItemDTO> itemsList,
        TaskType tType,
        DeliveryStatus deliveryTask,
        LocalDateTime createdAt,
        LocalDateTime completedAt) {
}
