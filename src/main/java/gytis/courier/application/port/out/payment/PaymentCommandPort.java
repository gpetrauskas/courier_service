package gytis.courier.application.port.out.payment;

import gytis.courier.domain.payment.Payment;

public interface PaymentCommandPort {
    void create(Payment payment);
    void update(Payment payment);
    void updateBasic(Payment payment);
    Payment findByOrderId(Long orderId);
    Payment findByOrderIdWithAttempts(Long orderId);
}
