package gytis.courier.application.service.paymentmethod;

import gytis.courier.application.port.in.paymentmethod.PaymentMethodQueryUseCase;
import gytis.courier.application.port.out.paymentmethod.PaymentMethodQueryPort;
import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodQueryService implements PaymentMethodQueryUseCase {
    private final PaymentMethodQueryPort port;

    public PaymentMethodQueryService(PaymentMethodQueryPort port) {
        this.port = port;
    }

    @Override
    public List<UserPaymentMethodReadModel> savedMethods(Long userId) {
        return port.getAll(userId);
    }

    @Override
    public UserPaymentMethodReadModel get(Long methodId, Long userId) {
        return port.findProjection(methodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Method not found"));
    }
}
