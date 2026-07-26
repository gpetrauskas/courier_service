package gytis.courier.application.port.out.payment;

import gytis.courier.domain.payment.Payment;

import java.util.Optional;

public interface PaymentCommandPort {
    void create(Payment payment);
    Payment update(Payment payment);
/*
    void updateBasic(Payment payment);
*/
    Optional<Payment> findByOrderIdLocked(Long orderId);
    Optional<Payment> findByOrderId(Long orderId);
/*
    Payment findByOrderIdWithAttempts(Long orderId);
*/
}
