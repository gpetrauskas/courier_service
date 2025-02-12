package com.example.courier.dto;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public record DeliveryTaskDTO(
        Long taskId,
        CourierDTO courierDTO,
        Long adminId,
        List<DeliveryTaskItemDTO> itemsList,
        TaskType tType,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime completedAt) {
}
