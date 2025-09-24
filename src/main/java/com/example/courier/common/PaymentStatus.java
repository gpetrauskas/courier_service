package com.example.courier.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents orders payment status.
 *
 *<p>Lifecycle:</p>
 * <ul>
 *     <li>{@code NOT_PAID} - the order has been created but no payment received (initial new payment status)</li>
 *     <li>{@code PAID} - the order was successfully paid.</li>
 *     <li>{@code FAILED} - the payment attempt did not succeed</li>
 *     <li>{@code CANCELED} - the order was canceled</li>
 * </ul>*/
public enum PaymentStatus {
    NOT_PAID,
    FAILED,
    PAID,
    CANCELED;

    private static final Set<String> STATUS_NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    /**
     * Validates that given status string corresponds to a defined PaymentStatus.
     *
     * @param status a string status to validate
     * @throws IllegalArgumentException if payment status is not recognized or null
     */
    public static void isValidStatus(String status) {
        if (status == null || !STATUS_NAMES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid payment status");
        }
    }
}
