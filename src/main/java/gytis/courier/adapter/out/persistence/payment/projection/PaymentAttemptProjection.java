package gytis.courier.adapter.out.persistence.payment.projection;

import gytis.courier.domain.payment.PaymentAttemptStatus;
import gytis.courier.domain.payment.ProviderType;

import java.time.LocalDateTime;

public interface PaymentAttemptProjection {
    Long getId();
    PaymentAttemptStatus getStatus();
    ProviderType getProviderType();
    String getTransactionId();
    String getFailureReason();
    LocalDateTime getCreatedAt();
    Long getPaymentId();
}
