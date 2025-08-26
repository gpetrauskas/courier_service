package com.example.courier.payment.handler;

import com.example.courier.domain.*;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.factory.PaymentMethodFactory;
import com.example.courier.payment.processor.PaymentProcessor;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.exception.ResourceNotFoundException;
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

    private final PaymentMethodFactory factory;
    private final PaymentProcessorRegistry processorRegistry;


    public NewPaymentMethodHandler(PaymentProcessorRegistry processorRegistry, PaymentMethodFactory factory) {
        this.processorRegistry = processorRegistry;
        this.factory = factory;
    }

    /**
     * Determines if this handler supports the given request by checking
     * if newPaymentMethod is not null and paymentMethodId is null
     *
     * @param requestDTO requested {@link PaymentRequestDTO} details
     * @return true if a new payment method is provided and no existing ID is set; false otherwise
     */
    @Override
    public boolean isSupported(PaymentRequestDTO requestDTO) {
        return requestDTO.newPaymentMethod() != null && requestDTO.paymentMethodId() == null;
    }

    /**
     * Sets up the provided payment method and charges the user.
     *
     * @param requestDTO the {@link PaymentRequestDTO} containing new payment method details
     * @param user the current user performing the payment
     * @param amount the amount to charge
     * @return  the result of payment processing operation
     * @throws UnsupportedOperationException if paypal method is requested (not implemented yet)
     * @throws IllegalArgumentException if unknown payment method type is provided
     * @throws ResourceNotFoundException if not matching processor is founded
     * */
    @Override
    public PaymentResultResponse handle(PaymentRequestDTO requestDTO, User user, BigDecimal amount) {
        PaymentMethod method = factory.create(requestDTO.newPaymentMethod(), user, requestDTO.cvc());
        return processorRegistry.getProcessor(method)
                .process(method, requestDTO, amount);
    }
}
