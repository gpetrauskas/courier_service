package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PaymentStatus {
    NOT_PAID,
    FAILED,
    PAID,
    CANCELED;

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    public static void isValidStatus(String status) {
        if (status != null && !STATUS_NAMES.contains(status)) {
            throw new IllegalArgumentException("Invalid payment status");
        }
    }

}
