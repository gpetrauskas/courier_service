package com.example.courier.common;

import com.example.courier.domain.PaymentAttempt;
import com.example.courier.domain.Payment;

/**
 * Represents a current status of a {@link PaymentAttempt}.
 *
 * <ul>
 *     <li>{@code PENDING} - initial status after the attempt has been created</li>
 *     <li>{@code FAILED} - the attempt was processed but failed</li>
 *     <li>{@code SUCCESS} - the attempt was processed successfully</li>
 * </ul>
 *
 * <p>This enum is used to track the lifecycle of payment attempts for a {@link Payment}</p>
 */
public enum PaymentAttemptStatus {
    PENDING,
    FAILED,
    SUCCESS;
}
