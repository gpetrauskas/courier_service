package gytis.courier.application.port.out.paymentmethod;

import gytis.courier.domain.payment.method.PaymentMethod;

import java.util.Optional;

public interface PaymentMethodCommandPort {
    Optional<PaymentMethod> findByIdAndUserId(Long id, Long userId);
}
