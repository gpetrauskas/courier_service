package gytis.courier.domain.payment;

import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.event.DomainEvent;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import gytis.courier.domain.event.PaymentFailedEvent;
import gytis.courier.exception.InvalidStateTransitionException;

import java.math.BigDecimal;
import java.util.*;

public class Payment {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private List<PaymentAttempt> paymentAttempts = new ArrayList<>();

    protected Payment() {
    }

    public static Payment create(Long orderId, BigDecimal amount) {
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(amount);

        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.amount = amount;
        payment.status = PaymentStatus.NOT_PAID;

        return payment;
    }

    public static Payment restore(
            Long id,
            Long orderId,
            BigDecimal amount,
            PaymentStatus status,
            List<PaymentAttempt> paymentAttempts
    ) {
        Payment p = new Payment();
        p.id = id;
        p.orderId = orderId;
        p.amount = amount;
        p.status = status;
        p.paymentAttempts = paymentAttempts != null
                ? new ArrayList<>(paymentAttempts)
                : new ArrayList<>();

        return p;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public List<PaymentAttempt> getPaymentAttempts() { return Collections.unmodifiableList(paymentAttempts); }

    public PaymentAttempt startAttempt(ProviderType providerType) {
        markAsProcessing();

        PaymentAttempt attempt = PaymentAttempt.pending(providerType);
        this.addAttempt(attempt);

        return attempt;
    }

    public Optional<DomainEvent> completeAttempt(PaymentAttempt attempt, PaymentResult result) {
        Objects.requireNonNull(attempt);
        Objects.requireNonNull(result);

        if (result.success()) {
            attempt.markSuccess(result.transactionId());
            return Optional.of(markAsPaid());
        } else {
            attempt.markFailure(result.failureReason());
            if (this.getPaymentAttempts().size() >= 5) {
                return Optional.of(markAsFailed());
            }
            revertToNotPaid();
            return Optional.empty();
        }
    }

    public void changeStatus(PaymentStatus newStatus) {
        if (!this.status.isFinalState()) {
            this.status = newStatus;
        } else {
            throw new InvalidStateTransitionException("Payment is in final state");
        }
    }

    public void cancel() {
        if (this.status.isFinalState()) {
            throw new IllegalStateException("Payment cannot be canceled");
        }

        this.status = PaymentStatus.CANCELED;
    }

    public PaymentConfirmedEvent markAsPaid() {
        if (!this.status.canTransit(PaymentStatus.PAID)) throw new IllegalStateException("Cannot transition to PAID");
        this.status = PaymentStatus.PAID;

        return new PaymentConfirmedEvent(orderId);
    }

    private void addAttempt(PaymentAttempt attempt) {
        Objects.requireNonNull(attempt);
        this.paymentAttempts.add(attempt);
    }

    private PaymentFailedEvent markAsFailed() {
        if (!this.status.canTransit(PaymentStatus.FAILED)) throw new IllegalStateException("Cannot transition to FAILED");
        this.status = PaymentStatus.FAILED;
        return new PaymentFailedEvent(orderId);
    }

    private void markAsProcessing() {
        if (!status.canTransit(PaymentStatus.PROCESSING)) {
            throw new IllegalStateException("Cannot pay: current status: " + this.status);
        }
        this.status = PaymentStatus.PROCESSING;
    }

    private void revertToNotPaid() {
        if (!status.canTransit(PaymentStatus.NOT_PAID)) {
            throw new IllegalStateException("Cannot revert to NOT_PAID from " + status);
        }

        this.status = PaymentStatus.NOT_PAID;
    }
}