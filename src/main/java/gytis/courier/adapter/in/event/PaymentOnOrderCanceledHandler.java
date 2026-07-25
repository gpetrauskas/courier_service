package gytis.courier.adapter.in.event;

import gytis.courier.application.port.in.payment.CancelPaymentUseCase;
import gytis.courier.domain.event.OrderCanceledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentOnOrderCanceledHandler {
    private final CancelPaymentUseCase useCase;

    public PaymentOnOrderCanceledHandler(CancelPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @EventListener
    public void handle(OrderCanceledEvent event) {
        useCase.cancelByOrderId(event.orderId());
    }
}
