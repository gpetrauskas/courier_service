package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a lifecycle states of an order.
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>{@code PENDING} - Initial order status after creation</li>
 *     <li>{@code CONFIRMED} - Status after payment was successfully done</li>
 *     <li>{@code CANCELED} - After order was canceled</li>
 * </ul>
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELED;

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    /**
     * Validates that the given string is contained by the valid order status.
     *
     * @param status the status string to validate
     * @throws IllegalArgumentException if status is null or not valid
     */
    public static void isValidStatus(String status) {
        if (status == null || !STATUS_NAMES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    /**
     * parses the given string into OrderStatus.
     *
     * @param status the status string
     * @return the matching OrderStatus
     * @throws IllegalStateException if the status is null or blank
     */
    public static OrderStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalStateException("Status is null");
        }

        isValidStatus(status);
        return OrderStatus.valueOf(status);
    }
}
