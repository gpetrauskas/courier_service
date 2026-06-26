package gytis.courier.application.readmodel.payment;

import gytis.courier.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public record AdminPaymentSummaryReadModel(
        Long id,
        PaymentStatus status,
        BigDecimal amount
) {
}
