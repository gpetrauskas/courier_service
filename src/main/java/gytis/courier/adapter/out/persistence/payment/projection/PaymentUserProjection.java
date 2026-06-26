package gytis.courier.adapter.out.persistence.payment.projection;

import gytis.courier.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentUserProjection {
    PaymentStatus getStatus();
    BigDecimal getAmount();
}
