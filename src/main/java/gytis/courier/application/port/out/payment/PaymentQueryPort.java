package gytis.courier.application.port.out.payment;

import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;

import java.util.Optional;

public interface PaymentQueryPort {
    Optional<UserPaymentSummaryReadModel> findUserProjectionByOrderId(Long orderId);
}
