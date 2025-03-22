package com.example.courier.dto.response.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.DeliveryTaskItemDTO;

import java.time.LocalDateTime;
import java.util.List;

public record AdminTaskDTO(
        Long taskId,
        CourierDTO courierDTO,
        Long adminId,
        List<DeliveryTaskItemDTO> itemsList,
        TaskType taskType,
        DeliveryStatus deliveryStatus,
        List<String> notes,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) implements TaskBase {
}
