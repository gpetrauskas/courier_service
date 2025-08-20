package com.example.courier.payment.handler;

import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;

/**
 * Defines the contract for handling different types of payment requests.
 *
 * Implementation decide whether they can process a given request
 * and perform the actual handling logic
 */
public interface PaymentHandler {

    /**
     * Determines whether this handler supports the given payment request
     *
     * @param paymentRequestDTO the incoming payment request
     * @return true if this handler can process the request, false otherwise
     */
    boolean isSupported(PaymentRequestDTO paymentRequestDTO);

    /**
     * Processes the given payment request
     *
     * @param paymentRequestDTO the incoming payment request
     * @return the results of the payment processing
     */
    PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO);
}
