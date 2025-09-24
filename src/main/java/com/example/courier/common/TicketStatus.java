package com.example.courier.common;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import com.example.courier.domain.Ticket;

/**
 * Represents status of a {@link Ticket}.
 *
 * <ul>
 *     <li>{@code OPEN} - initial status after a ticket has been created</li>
 *     <li>{@code IN_PROGRESS} - ticket is being worked on</li>
 *     <li>{@code RESOLVED} - ticket issues has been resolved</li>
 *     <li>{@code CLOSED} - ticket is closed</li>
 * </ul>*/
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

    private static final Set<TicketStatus> CLOSED_STATUSES = Set.of(RESOLVED, CLOSED);

    /**
     * Checks if a transition from the current status to the proposed one is valid.
     *
     * @param current current status
     * @param proposed a proposed status
     * @return true if valid and false if not
     */
    public static boolean isValidTransition(TicketStatus current, TicketStatus proposed) {
        return VALID_TRANSITIONS.getOrDefault(current, Set.of())
                .contains(proposed);
    }

    /**
     * Checks if given string match any of enum name.
     *
     * @param status a status as a string
     * @return true if string matches a valid status and false if not
     */
    public static boolean isValidStatus(String status) {
        return Arrays.stream(TicketStatus.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(status));
    }

    /**
     * Checks if a */
    public boolean isClosed() {
        return CLOSED_STATUSES.contains(this);
    }
}
