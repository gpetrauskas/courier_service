package gytis.courier.application.port.out;

import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.payment.method.PaymentMethod;

public interface PaymentProcessorGateway {
    boolean supports(PaymentMethod method);
    PaymentResult process(PaymentMethod method, String cvc);
}
