package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TicketPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT;

    private static final Set<String> VALID_NAMES =
            Arrays.stream(values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    public static boolean isValidPriority(String priority) {
        return priority != null && VALID_NAMES.contains(priority);
    }
}
