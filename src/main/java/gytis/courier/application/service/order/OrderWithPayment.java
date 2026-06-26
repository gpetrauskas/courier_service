package gytis.courier.application.service.order;

import gytis.courier.domain.order.Order;
import gytis.courier.domain.payment.Payment;

public record OrderWithPayment(
        Order order,
        Payment payment
) {
}
