package com.example.courier.payment.processor;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.service.permission.PermissionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * {@link PaymentProcessor} implementation for handling {@link CreditCard} payments.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Identify {@link CreditCard} as a supported type</li>
 *   <li>Verify that the current user has permission to use the card</li>
 *   <li>Charge the specified amount via {@link CreditCardService}</li>
 * </ul>
 */
@Component
public class CreditCardProcessor implements PaymentProcessor<CreditCard> {
    private final CreditCardService service;
    private final PermissionService permissionService;

    public CreditCardProcessor(CreditCardService service, PermissionService permissionService) {
        this.service = service;
        this.permissionService = permissionService;
    }

    /**
     * Supports only {@link CreditCard} payment methods
     *
     * @param paymentMethod the method to check for compatibility
     * @return true if payment method is instance of CreditCard, false otherwise
     */
    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod instanceof CreditCard;
    }

    /**
     * Processes a credit card payment after validating permission
     *
     * <p>This method:
     * <ol>
     *   <li>Ensures the current user has access to the specified card</li>
     *   <li>Charges the card immediately using {@link CreditCardService}</li>
     * </ol>
     * @param card the payment method to process. must be of type {@link CreditCard}
     * @param paymentRequestDTO payment details containing transaction details and cvc
     * @param amount amount to charge
     * @return the result of the payment processing operation
     * @throws UnauthorizedAccessException if user has no access to the credit card
     */
    @Override
    public PaymentResultResponse process(CreditCard card, PaymentRequestDTO paymentRequestDTO, BigDecimal amount) {
        if (!permissionService.hasPaymentMethodAccess(card)) {
            throw new UnauthorizedAccessException("Credit card does not belong to current user");
        }
        return service.chargeSavedCard(card, paymentRequestDTO.cvc(), amount);
    }
}
