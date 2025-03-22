package com.example.courier.common;

import com.example.courier.domain.TaskItem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());


    public static List<ParcelStatus> getStatusesPreventingRemoval() {
        return List.of(CANCELED, REMOVED_FROM_THE_LIST);
    }

    public static void isValidStatus(String status) {
        if (status != null && !STATUS_NAMES.contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public static boolean isValidStatusChange(TaskType taskType, ParcelStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(taskType, Set.of()).contains(newStatus);
    }

    public boolean isFinalState() {
        return Set.of(PICKED_UP, DELIVERED, CANCELED).contains(this);
    }

    public static boolean isItemInFinalState(TaskItem taskItem) {
        return taskItem.getStatus().isFinalState();
    }

}
