package com.example.courier.common;

import java.util.Objects;

public enum TaskType {
    DELIVERY,
    PICKUP;

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
