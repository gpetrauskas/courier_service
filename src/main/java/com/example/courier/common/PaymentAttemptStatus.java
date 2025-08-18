package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PaymentAttemptStatus {
    PENDING,
    FAILED,
    SUCCESS;

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static void isValid(String status) {
        if (status != null && !STATUS_NAMES.contains(status)) {
            throw new IllegalArgumentException("invalid payment attempt status");
        }
    }
}
