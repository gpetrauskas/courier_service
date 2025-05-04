package com.example.courier.common;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    private static final Map<TicketStatus, Set<TicketStatus>> VALID_TRANSITIONS = Map.of(
            OPEN, Set.of(IN_PROGRESS, CLOSED),
            IN_PROGRESS, Set.of(RESOLVED, CLOSED),
            RESOLVED, Set.of(CLOSED),
            CLOSED, Set.of()
    );

    public static boolean isValidTransition(TicketStatus current, TicketStatus proposed) {
        return VALID_TRANSITIONS.getOrDefault(current, Set.of())
                .contains(proposed);
    }
}
