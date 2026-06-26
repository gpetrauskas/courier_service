package gytis.courier.application.event;

import gytis.courier.application.port.in.order.AdminOrderUpdateUseCase;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderOnPaymentConfirmedHandler {
    private final AdminOrderUpdateUseCase useCase;

    public OrderOnPaymentConfirmedHandler(AdminOrderUpdateUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(PaymentConfirmedEvent event) {
        useCase.markAsPaid(event.orderId());
    }
}
