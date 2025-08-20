package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
/**
 * Defines contract for handling a specific type of payment method
 * Implementations decide if they support a given method and process it
 */
public interface PaymentProcessor {

    /**
     * Checks if this processor supports the given {@link PaymentMethod} type
     *
     * @param paymentMethod the method to check
     * @return true if supported, false otherwise
     */
    boolean supports(PaymentMethod paymentMethod);

    /**
     * Processes the given payment request
     *
     * @param paymentMethod the method to use for processing
     * @param paymentRequestDTO transaction details
     * @return result of processing
     */
    PaymentResultResponse process(PaymentMethod paymentMethod, PaymentRequestDTO paymentRequestDTO);
}
