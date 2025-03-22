package com.example.courier.validation;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskItemValidator {

    public static void validateItemCanBeRemovedFromTask(Task task, Long itemId) {
        boolean isValid = task.getItems().stream()
                .anyMatch(item ->
                        item.getId().equals(itemId));

        if (!isValid || !task.getDeliveryStatus().equals(DeliveryStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("The item does not belong to specified task or task is not in progress");

        }
    }
}
