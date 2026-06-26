package gytis.courier.application.event;

import gytis.courier.application.port.in.payment.CancelPaymentUseCase;
import gytis.courier.domain.event.OrderCanceledEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentOnOrderCanceledHandler {
    private final CancelPaymentUseCase useCase;

    public PaymentOnOrderCanceledHandler(CancelPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCanceledEvent event) {
        useCase.cancelByOrderId(event.orderId());
    }
}
