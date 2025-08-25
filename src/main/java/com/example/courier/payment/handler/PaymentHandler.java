package com.example.courier.payment.handler;

import com.example.courier.domain.User;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;

import java.math.BigDecimal;

/**
 * Defines the contract for handling different types of payment requests.
 *
 * <p>Implementations:
 * <ul>
 *   <li>Indicate whether they can handle a given request via {@link #isSupported(PaymentRequestDTO)}</li>
 *   <li>Perform the request handling and delegate actual charging to a {@code PaymentProcessor}</li>
 * </ul>
 */
public interface PaymentHandler {

    /**
     * Determines whether this handler supports the given payment request
     *
     * @param paymentRequestDTO the incoming payment request
     * @return {@code true} if this handler supports the request; {@code false} otherwise
     * */
    boolean isSupported(PaymentRequestDTO paymentRequestDTO);

    /**
     * Handles the payment request for a specific user and amount.
     *
     * @param paymentRequestDTO the request details
     * @param user the user making the payment
     * @param amount the transaction amount
     * @return the result of payment processing
     */
    PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO, User user, BigDecimal amount);
}
