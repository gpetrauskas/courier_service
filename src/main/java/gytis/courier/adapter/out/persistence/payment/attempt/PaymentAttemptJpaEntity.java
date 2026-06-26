package gytis.courier.adapter.out.persistence.payment.attempt;

import gytis.courier.adapter.out.persistence.payment.PaymentJpaEntity;
import gytis.courier.domain.payment.PaymentAttemptStatus;
import gytis.courier.domain.payment.ProviderType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_attempts")
public class PaymentAttemptJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private PaymentAttemptStatus status;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(updatable = false)
    private String transactionId;

    @Column(updatable = false)
    private String failureReason;

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id")
    private PaymentJpaEntity payment;

    public PaymentAttemptJpaEntity() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public PaymentAttemptStatus getStatus() { return status; }
    public ProviderType getProviderType() { return providerType; }
    public String getTransactionId() { return transactionId; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
/*    public PaymentJpaEntity getPayment() { return payment; }*/

    public void setStatus(PaymentAttemptStatus status) { this.status = status; }
    public void setProviderType(ProviderType providerType) { this.providerType = providerType; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public void setPayment(PaymentJpaEntity payment) { this.payment = payment; }
}
