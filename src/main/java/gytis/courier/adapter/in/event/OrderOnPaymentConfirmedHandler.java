package gytis.courier.adapter.in.event;

import gytis.courier.application.port.in.order.AdminOrderUpdateUseCase;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderOnPaymentConfirmedHandler {
    private final AdminOrderUpdateUseCase useCase;

    public OrderOnPaymentConfirmedHandler(AdminOrderUpdateUseCase useCase) {
        this.useCase = useCase;
    }

    @EventListener
    public void handle(PaymentConfirmedEvent event) {
        useCase.markAsPaid(event.orderId());
    }
}
