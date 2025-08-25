package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;

import java.math.BigDecimal;

/**
 * Defines contract for handling a specific type of {@link PaymentMethod}
 * <p>Each implementation is responsible for:
 * <ul>
 *   <li>Declaring which payment method types it supports</li>
 *   <li>Executing payment logic for that type</li>
 * </ul>
 *
 * @param <T> the concrete {@link PaymentMethod} this processor handle
 */
public interface PaymentProcessor<T extends PaymentMethod> {

    /**
     * Checks if this processor supports the given {@link PaymentMethod} type
     *
     * @param paymentMethod the method to check
     * @return {@code true} if this processor can handle the provided method, else {@code false}
     * */
    boolean supports(PaymentMethod paymentMethod);

    /**
     * Processes the given payment request
     *
     * @param paymentMethod the method to use for processing
     * @param requestDTO transaction details
     * @param amount the transaction amount
     * @return a {@link PaymentResultResponse} result of processing
     */
    PaymentResultResponse process(T paymentMethod, PaymentRequestDTO requestDTO, BigDecimal amount);
}
