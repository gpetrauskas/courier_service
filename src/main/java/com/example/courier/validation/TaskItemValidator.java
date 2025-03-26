package com.example.courier.validation;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import org.springframework.stereotype.Component;

@Component
public class TaskItemValidator {

    public void validateItemCanBeRemovedFromTask(Task task, Long itemId) {
        boolean isValid = task.getItems().stream()
                .anyMatch(item ->
                        item.getId().equals(itemId));

        if (!isValid || !task.getDeliveryStatus().equals(DeliveryStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("The item does not belong to specified task or task is not in progress");
        }
    }

    public void validateNotInFinalState(TaskItem taskItem) {
        if (taskItem.getStatus().isFinalState()) {
            throw new IllegalArgumentException("Task Item cannot be updated anymore.");
        }
    }

    public void validateTransitionRule(TaskItem item, ParcelStatus newStatus) {
        if (!item.getStatus().isValidTransition(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid %s transition: %s -> %s",
                            item.getTask().getTaskType(),
                            item.getStatus(),
                            newStatus)
            );
        }
    }
}
