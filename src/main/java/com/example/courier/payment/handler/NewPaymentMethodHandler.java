package com.example.courier.payment.handler;

import com.example.courier.domain.*;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.payment.method.CreditCardService;
import org.springframework.stereotype.Component;

@Component
public class NewPaymentMethodHandler implements PaymentHandler {

    private final CreditCardService creditCardService;
    private final PaymentProcessorRegistry processorRegistry;

    public NewPaymentMethodHandler(PaymentProcessorRegistry processorRegistry,
                                   CreditCardService creditCardService) {
        this.processorRegistry = processorRegistry;
        this.creditCardService = creditCardService;
    }

    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.newPaymentMethod() != null;
    }

    @Override
    public PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO) {
        PaymentMethodDTO paymentMethodDTO = paymentRequestDTO.newPaymentMethod();
        PaymentMethod paymentMethod = switch (paymentMethodDTO) {
            case CreditCardDTO ccDTO -> creditCardService.setupCreditCard(ccDTO);
            case PayPalDTO ppDTO -> throw new UnsupportedOperationException("Paypal not supported yet");
            default -> throw new IllegalArgumentException("Unknown payment method: " + paymentMethodDTO.getClass());
        };

        return processorRegistry.getProcessor(paymentMethod)
                .process(paymentMethod, paymentRequestDTO);

    }

 /*  // @Override
    public PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO, Payment payment) {
        User user = payment.getOrder().getUser();
        CreditCardDTO creditCardDTO = (CreditCardDTO) paymentRequestDTO.newPaymentMethod();
        CreditCard card = creditCardService.setupCreditCard(creditCardDTO, user);

        PaymentResultResponse response = creditCardService.paymentTest(card, creditCardDTO.cvc());
        if (!response.status().equals("success")) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new PaymentFailedException(response.message());
        }

        if (!card.isSaved()) {
            paymentMethodRepository.saveAndFlush(creditCardService.dontSaveCreditCard(card));
        } else {
            paymentMethodRepository.saveAndFlush(card);
        }

        payment.setPaymentMethod(card);
        return new PaymentResultResponse("success", "Payment done successfully", null, null);
    }*/
}
