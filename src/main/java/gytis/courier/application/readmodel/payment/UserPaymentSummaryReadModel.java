package gytis.courier.application.readmodel.payment;

import java.math.BigDecimal;

public record UserPaymentSummaryReadModel(
        String status,
        BigDecimal amount
) {
}
