package com.example.courier.common;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum DeliveryStatus {
    IN_PROGRESS,
    COMPLETED,
    ASSIGNED,
    CANCELED,
    RETURNING_TO_STATION,
    AT_CHECKPOINT;

    public static Set<DeliveryStatus> currentStatuses() {
        return EnumSet.of(IN_PROGRESS, RETURNING_TO_STATION);
    }

    public static Set<DeliveryStatus> historicalStatuses() {
        return EnumSet.of(COMPLETED, CANCELED);
    }

    public static boolean isCheckInAllowed(DeliveryStatus status) {
        return status == RETURNING_TO_STATION;
    }

    public static boolean isAdminUpdatable(DeliveryStatus status) {
        return status == AT_CHECKPOINT || status == IN_PROGRESS;
    }

    public static boolean isTaskItemUpdatable(DeliveryStatus status) {
        return status == IN_PROGRESS;
    }

    public static DeliveryStatus validateAndGetStatus(String status) {
        return Arrays.stream(DeliveryStatus.values())
                .filter(e -> e.name().equals(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
    }

    public boolean isFinalState() {
        return historicalStatuses().contains(this);
    }
}
