package gytis.courier.domain.payment;

import java.time.LocalDateTime;

public class PaymentAttempt {
    private Long id;
    private PaymentAttemptStatus status;
    private ProviderType provider;
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;

    protected PaymentAttempt() {}

    public static PaymentAttempt pending(ProviderType provider) {
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.status = PaymentAttemptStatus.PENDING;
        attempt.provider = provider;
        attempt.transactionId = "";
        return attempt;
    }

    public static PaymentAttempt restore(
            Long id,
            PaymentAttemptStatus status,
            ProviderType provider,
            String transactionId,
            String failureReason,
            LocalDateTime createdAt
    ) {
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.id = id;
        attempt.status = status;
        attempt.provider = provider;
        attempt.transactionId = transactionId;
        attempt.failureReason = failureReason;
        attempt.createdAt = createdAt;
        return attempt;
    }

    public void markSuccess(String txId) {
        this.status = PaymentAttemptStatus.SUCCESS;
        this.transactionId = txId;
    }

    public void markFailure(String reason) {
        this.status = PaymentAttemptStatus.FAILED;
        this.failureReason = reason;
    }

    public Long getId() { return id; }
    public PaymentAttemptStatus getStatus() { return status; }
    public ProviderType getProvider() { return provider; }
    public String getTransactionId() { return transactionId; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
