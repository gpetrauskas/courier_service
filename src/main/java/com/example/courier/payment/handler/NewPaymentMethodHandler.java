package com.example.courier.payment.handler;

import com.example.courier.domain.*;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.exception.ResourceNotFoundException;
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

    /**
     * Determines if this handler supports the given request by checking
     * if newPaymentMethod is not null
     *
     * @param paymentRequestDTO requested {@link PaymentRequestDTO} details
     * @return true if newPaymentMethod is not null; false if its null
     */
    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.newPaymentMethod() != null;
    }

    /**
     * Handles a payment request using newly provided payment method
     *
     * It supports different payment method types through pattern matching:
     * CreditCard: sets up new {@link CreditCard} payment method via creditCardService
     * PayPal:
     *
     * After payment method setup, delegates processing to the appropriate processor from the registry
     *
     * @param paymentRequestDTO the {@link PaymentRequestDTO} containing new payment method details
     * @return PaymentResultResponse the result of payment processing operation
     * @throws UnsupportedOperationException if paypal method is requested (not implemented yet)
     * @throws IllegalArgumentException if unknown payment method type is provided
     * @throws ResourceNotFoundException if not matching processor is founded
     * */
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
}
