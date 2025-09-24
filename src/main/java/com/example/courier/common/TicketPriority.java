package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents priority level of a ticket.
 *
 * <ul>
 *     <li>{@code LOW} - minor issues, low urgency</li>
 *     <li>{@code NORMAL} - standard priority</li>
 *     <li>{@code HIGH} - important issue that need quick attention</li>
 *     <li>{@code URGENT} - critical issues requiring immediate action</li>
 * </ul>*/
public enum TicketPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT;

    private static final Set<String> VALID_NAMES =
            Arrays.stream(values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    /**
     * Checks if given string is a valid priority name.
     *
     * @param priority the priority name to validate
     * @return true if the name matches enum constants
     */
    public static boolean isValidPriority(String priority) {
        return priority != null && VALID_NAMES.contains(priority);
    }
}
