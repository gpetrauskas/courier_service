package gytis.courier.application.port.in.payment;

import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;

public interface PaymentQueryUseCase {
    UserPaymentSummaryReadModel get(Long orderId);
}
