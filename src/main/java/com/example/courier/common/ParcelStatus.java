package com.example.courier.common;

import com.example.courier.domain.DeliveryTaskItem;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public enum ParcelStatus {
    WAITING_FOR_PAYMENT,
    PICKING_UP,
    DELIVERING,
    PICKED_UP,
    DELIVERED,
    AT_CHECKPOINT,
    FAILED_PICKUP,
    FAILED_DELIVERY,
    CANCELED,
    REMOVED_FROM_THE_LIST,
    RETURNED_TO_CHECKPOINT,
    NOT_SHIPPED;

    private static final Map<TaskType, Set<ParcelStatus>> VALID_TRANSITIONS = Map.of(
            TaskType.PICKUP, Set.of(PICKED_UP, PICKING_UP, CANCELED, FAILED_PICKUP),
            TaskType.DELIVERY, Set.of(DELIVERED, DELIVERING, CANCELED, FAILED_DELIVERY)
    );

    public static boolean isValidStatus(String status) {
        return Arrays.stream(values())
                .anyMatch(parcelStatus -> parcelStatus.name().equalsIgnoreCase(status));
    }

    public static boolean isValidStatusChange(TaskType taskType, ParcelStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(taskType, Set.of()).contains(newStatus);
    }

    public boolean isFinalState() {
        return Set.of(PICKED_UP, DELIVERED, CANCELED).contains(this);
    }

    public static boolean isItemInFinalState(DeliveryTaskItem taskItem) {
        return taskItem.getStatus().isFinalState();
    }


}
