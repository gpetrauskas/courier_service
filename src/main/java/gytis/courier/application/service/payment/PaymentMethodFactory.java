package gytis.courier.application.service.payment;

import gytis.courier.application.command.CreditCardCommand;
import gytis.courier.application.command.PaymentMethodCommand;
import gytis.courier.application.command.PaypalCommand;
import gytis.courier.domain.payment.method.CreditCard;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.domain.payment.method.Paypal;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodFactory {
    public PaymentMethod from(PaymentMethodCommand command) {
        return switch (command) {
            case CreditCardCommand cc -> CreditCard.create(cc.cardNumber(), cc.cardHolderName(), cc.expiryDate(), cc.saveCard());
            case PaypalCommand pp -> Paypal.create(pp.ppEmail(), pp.saved());
        };
    }
}
