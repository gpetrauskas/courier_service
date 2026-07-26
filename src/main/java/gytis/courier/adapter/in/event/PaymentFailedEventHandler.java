package gytis.courier.adapter.in.event;

import gytis.courier.application.port.in.order.AdminOrderUpdateUseCase;
import gytis.courier.domain.event.PaymentFailedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedEventHandler {
    private final AdminOrderUpdateUseCase useCase;

    public PaymentFailedEventHandler(AdminOrderUpdateUseCase useCase) {
        this.useCase = useCase;
    }

    @EventListener
    public void handle(PaymentFailedEvent event) {
        useCase.markAsCanceled(event.orderId());
    }
}
