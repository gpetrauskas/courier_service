package gytis.courier.adapter.out.gateway;

import gytis.courier.application.port.out.PaymentProcessorGateway;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.payment.ProviderType;
import gytis.courier.domain.payment.method.CreditCard;
import gytis.courier.domain.payment.method.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreditCardGateway implements PaymentProcessorGateway {
    private final static ProviderType PROVIDER_TYPE = ProviderType.CREDIT_CARD;
    private final static String STOLEN_CARD_SUFFIX = "9999";
    private final static String INSUFFICIENT_FUNDS = "0000";
    private final static String CVC_INvALID = "000";


    @Override
    public boolean supports(PaymentMethod method) {
        return method instanceof CreditCard;
    }


    @Override
    public PaymentResult process(PaymentMethod method, String cvc) {
        CreditCard card = (CreditCard) method;

        if (card.getToken() != null) {
            return processWithToken(card, cvc);
        }

        return processNewCard(card, cvc);
    }

    private PaymentResult processWithToken(CreditCard card, String cvc) {
        String transactionId = "cc_tx_" + UUID.randomUUID();
        return new PaymentResult(PROVIDER_TYPE, transactionId, true, null, false, null);
    }

    private PaymentResult processNewCard(CreditCard card, String cvc) {
        String failed = simulateProviderFailure(card, cvc);
        if (failed != null) {
            return new PaymentResult(PROVIDER_TYPE, null, false, failed, false, null);
        }

        String token = null;
        boolean saved = false;

        if (card.isSaved()) {
            token = "tok_" + UUID.randomUUID();
            saved = true;
        }

        String transactionId = "cc_tx_" + UUID.randomUUID();
        return new PaymentResult(PROVIDER_TYPE, transactionId, true, null, saved, token);
    }

    private String simulateProviderFailure(CreditCard cc, String cvc) {
        if (CVC_INvALID.equals(cvc)) {
            return "Invalid credit card CVC";
        }

        if (cc.getCardNumber().endsWith(STOLEN_CARD_SUFFIX)) {
            return "Credit card was marked as stolen";
        }

        if (cc.getCardNumber().startsWith(INSUFFICIENT_FUNDS)) {
            return "Credit card has insufficient funds";
        }

        return null;
    }
}
