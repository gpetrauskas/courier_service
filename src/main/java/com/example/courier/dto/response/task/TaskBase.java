package com.example.courier.dto.response.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;

import java.time.LocalDateTime;

public sealed interface TaskBase permits CourierTaskDTO, AdminTaskDTO {
    Long taskId();
    TaskType taskType();
    DeliveryStatus deliveryStatus();
    LocalDateTime createdAt();
    LocalDateTime completedAt();

}
