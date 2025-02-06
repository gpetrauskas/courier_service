package com.example.courier.dto;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;

public record DeliveryTaskFilterDTO(int page, int size, Long courierId, Long taskListId,
                                    TaskType taskType, DeliveryStatus deliveryStatus) {
    public DeliveryTaskFilterDTO {
        if (page < 0) throw new IllegalArgumentException("Page cannot be less than 0");
        if (size <= 0) throw new IllegalArgumentException("Size must me more than 1");
    }
}
