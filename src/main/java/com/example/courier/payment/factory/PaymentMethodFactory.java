package com.example.courier.payment.factory;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.OneTimeCard;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.payment.method.PayPalService;
import com.example.courier.validation.payment.CreditCardValidator;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodFactory {
    private final CreditCardService creditCardService;
    private final CreditCardValidator creditCardValidator;
    private final PayPalService payPalService;

    public PaymentMethodFactory(CreditCardService creditCardService, CreditCardValidator creditCardValidator,
                                PayPalService payPalService) {
        this.creditCardService = creditCardService;
        this.creditCardValidator = creditCardValidator;
        this.payPalService = payPalService;
    }

    public PaymentMethod create(PaymentMethodDTO dto, User user, String cvc) {
        return switch (dto) {
            case CreditCardDTO ccDTO -> {
                creditCardValidator.validateForSetup(ccDTO, cvc);
                if (ccDTO.saveCard()) {
                    yield creditCardService.setupCreditCard(ccDTO, cvc, user);
                } else {
                    yield new OneTimeCard(ccDTO.cardNumber(), ccDTO.expiryDate(), ccDTO.cardHolderName());
                }
            }
            case PayPalDTO payPalDTO -> throw new UnsupportedOperationException("Paypal not supported yet");
            default -> throw new IllegalArgumentException("Unknown method payment: " + dto.getClass());
        };
    }
}
