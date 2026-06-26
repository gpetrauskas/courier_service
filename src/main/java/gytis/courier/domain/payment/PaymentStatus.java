package gytis.courier.domain.payment;

import java.util.Set;

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

    private static final Set<PaymentStatus> FINAL_STATE = Set.of(PAID, CANCELED);

    public boolean isFinalState() {
        return FINAL_STATE.contains(this);
    }
}
