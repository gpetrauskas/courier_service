package com.example.courier.payment.processor;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.service.security.CurrentPersonService;
import org.springframework.stereotype.Component;

@Component
public class CreditCardProcessor implements PaymentProcessor {

    private final CreditCardService service;
    private final CurrentPersonService currentPersonService;

    public CreditCardProcessor(CreditCardService service, CurrentPersonService currentPersonService) {
        this.service = service;
        this.currentPersonService = currentPersonService;
    }

    /*
    * Returns true if payment method is instance of CreditCard
    */
    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod instanceof CreditCard;
    }

    /*
    * Cast generic PaymentMethod to CreditCard subtype
    * processor already knows it support this type (checked in supports() method)
    */
    @Override
    public PaymentResultResponse process(PaymentMethod paymentMethod, PaymentRequestDTO paymentRequestDTO) {
        CreditCard cc = (CreditCard) paymentMethod;
        if (!cc.getUser().getId().equals(currentPersonService.getCurrentPersonId())) {
            throw new UnauthorizedAccessException("Credit card does not belong to current user");
        }
        return service.paymentTest(cc, paymentRequestDTO.cvc());
    }
}
