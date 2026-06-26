package gytis.courier.adapter.out.gateway;

import gytis.courier.application.port.out.PaymentProcessorGateway;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.payment.ProviderType;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.domain.payment.method.Paypal;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaypalGateway implements PaymentProcessorGateway {
    @Override
    public boolean supports(PaymentMethod method) {
        return method instanceof Paypal;
    }

    @Override
    public PaymentResult process(PaymentMethod method, String ignoredCvc) {
        Paypal paypal = (Paypal) method;

        if (paypal.getPpEmail().contains("simulateBanned")) {
            return new PaymentResult(ProviderType.PAYPAL, null, false, "User is banned", paypal.isSaved(), null);
        }

        String transactionId = "pp_tx_" + UUID.randomUUID();
        boolean success = true;

        return new PaymentResult(ProviderType.PAYPAL, transactionId, success, null, paypal.isSaved(), null);
    }
}
