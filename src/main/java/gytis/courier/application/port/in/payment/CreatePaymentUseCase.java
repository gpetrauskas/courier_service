package gytis.courier.application.port.in.payment;

import java.math.BigDecimal;

public interface CreatePaymentUseCase {
    void create(Long orderId, BigDecimal amount);
}
