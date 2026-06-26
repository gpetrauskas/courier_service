package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentProjection {
    Long getId();
    PaymentStatus getStatus();
    BigDecimal getAmount();
}
