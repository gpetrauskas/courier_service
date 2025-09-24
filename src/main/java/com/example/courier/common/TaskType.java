package com.example.courier.common;

import java.util.Objects;

/**
 * Type of the task.
 *
 * <ul>
 *     <li>{@code PICKUP} - task type for picking up parcels from the senders</li>
 *     <li>{@code DELIVERY} - task type for delivering parcels to the recipients</li>
 * </ul>*/
public enum TaskType {
    DELIVERY,
    PICKUP;

    /**
     * Returns valid TaskType from a string
     *
     * @param type task type as a string
     * @return a valid task type
     * @throws IllegalArgumentException if status is unknown
     */
    public static TaskType fromString(String type) {
        Objects.requireNonNull(type, "Task type cannot be null");
        String norm = type.trim().toUpperCase();

        try {
            return valueOf(norm);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown task type: " + type, ex);
        }
    }
}
