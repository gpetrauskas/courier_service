package gytis.courier.application.port.in.person;

import gytis.courier.domain.payment.method.PaymentMethod;

public interface SaveNewPaymentMethodUseCase {
    void save(Long userId, PaymentMethod method, String token);
}
