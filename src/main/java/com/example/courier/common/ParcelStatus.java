package com.example.courier.common;

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

    private static final Set<ParcelStatus> FINAL_STATES = Set.of(PICKED_UP, DELIVERED, CANCELED, FAILED_PICKUP, FAILED_DELIVERY);


    public static List<ParcelStatus> getStatusesPreventingRemoval() {
        return List.of(CANCELED, REMOVED_FROM_THE_LIST);
    }

    public static void validateStatus(String status) {
        if (status != null && !STATUS_NAMES.contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public boolean isValidStatusChange(TaskType taskType, ParcelStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(taskType, Set.of()).contains(newStatus);
    }

    public boolean isFinalState() {
        return FINAL_STATES.contains(this);
    }

    public boolean isValidTransition(ParcelStatus newStatus) {
        return switch (this) {
            case PICKING_UP -> newStatus == PICKED_UP || newStatus == FAILED_PICKUP;
            case DELIVERING -> newStatus == DELIVERED || newStatus == FAILED_DELIVERY;
            case DELIVERED, PICKED_UP, FAILED_DELIVERY, FAILED_PICKUP -> false;
            default -> throw new IllegalArgumentException("invalid transition from: " + newStatus);
        };
    }

}
