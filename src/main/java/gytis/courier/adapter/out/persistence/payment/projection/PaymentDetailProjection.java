package gytis.courier.adapter.out.persistence.payment.projection;

import gytis.courier.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentDetailProjection {
    Long getId();
    Long getOrderId();
    Long getPaymentMethodId();
    BigDecimal getAmount();
    PaymentStatus getStatus();

}
