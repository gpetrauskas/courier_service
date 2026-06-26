package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.payment.attempt.PaymentAttemptJpaEntity;
import gytis.courier.domain.payment.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAttemptJpaEntity> attempts;
    
    protected PaymentJpaEntity() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public List<PaymentAttemptJpaEntity> getAttempts() { return attempts; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setAttempts(List<PaymentAttemptJpaEntity> attempts) { this.attempts = attempts; }
}
