package com.example.courier.payment.handler;

import com.example.courier.domain.*;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.processor.PaymentProcessor;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.payment.method.CreditCardService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Handles payment requests that include a new payment method
 * (e.g. user provides a new credit card or PayPal account during checkout).
 *
 * <p>Flow:
 * <ol>
 *   <li>Check if {@code newPaymentMethod} is present in the request</li>
 *   <li>Instantiate and persist the new {@link PaymentMethod} via the appropriate service</li>
 *   <li>Delegate charging to the correct {@link PaymentProcessor} from the registry</li>
 * </ol>
 */
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
     * Sets up the provided payment method and charges the user.
     *
     * @param paymentRequestDTO the {@link PaymentRequestDTO} containing new payment method details
     * @return  the result of payment processing operation
     * @throws UnsupportedOperationException if paypal method is requested (not implemented yet)
     * @throws IllegalArgumentException if unknown payment method type is provided
     * @throws ResourceNotFoundException if not matching processor is founded
     * */
    @Override
    public PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO, User user, BigDecimal amount) {
        PaymentMethodDTO paymentMethodDTO = paymentRequestDTO.newPaymentMethod();
        PaymentMethod paymentMethod = switch (paymentMethodDTO) {
            case CreditCardDTO ccDTO -> creditCardService.setupCreditCard(ccDTO, paymentRequestDTO.cvc(), user);
            case PayPalDTO ppDTO -> throw new UnsupportedOperationException("Paypal not supported yet");
            default -> throw new IllegalArgumentException("Unknown payment method: " + paymentMethodDTO.getClass());
        };

        return processorRegistry.getProcessor(paymentMethod)
                .process(paymentMethod, paymentRequestDTO, amount);
    }
}
