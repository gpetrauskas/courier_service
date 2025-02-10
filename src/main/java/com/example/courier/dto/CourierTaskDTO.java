package com.example.courier.dto;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public record CourierTaskDTO(Long courierId, TaskType taskType, DeliveryStatus deliveryStatus,
                             LocalDateTime createdAt, LocalDateTime completedAt) {
}
