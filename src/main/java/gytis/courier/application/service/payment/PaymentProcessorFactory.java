package gytis.courier.application.service.payment;

import gytis.courier.application.port.out.PaymentProcessorGateway;
import gytis.courier.domain.payment.method.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentProcessorFactory {
    private final List<PaymentProcessorGateway> processors;

    public PaymentProcessorFactory(List<PaymentProcessorGateway> processors) {
        this.processors = processors;
    }

    public PaymentProcessorGateway getProcessor(PaymentMethod method) {
        return processors.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No processor found " + method.getClass().getSimpleName()));
    }
}
