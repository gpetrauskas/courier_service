package gytis.courier.application.service.payment;

import gytis.courier.application.port.in.payment.PaymentQueryUseCase;
import gytis.courier.application.port.out.payment.PaymentQueryPort;
import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PaymentQueryService implements PaymentQueryUseCase {
    private final PaymentQueryPort port;

    public PaymentQueryService(PaymentQueryPort port) {
        this.port = port;
    }

    @Override
    public UserPaymentSummaryReadModel get(Long orderId) {
        return port.findUserProjectionByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment info not found"));
    }
}
