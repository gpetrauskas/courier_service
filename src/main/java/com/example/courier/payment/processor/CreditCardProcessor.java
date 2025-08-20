package com.example.courier.payment.processor;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.service.permission.PermissionService;
import org.springframework.stereotype.Component;

/**
 * Payment processor for handling credit cards
 */
@Component
public class CreditCardProcessor implements PaymentProcessor {

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
     * Cast generic PaymentMethod to CreditCard subtype
     * processor already knows it support this type (checked in supports() method)
     * Validates credit card ownership via permission service
     * Process the payment
     *
     * @param paymentMethod the payment method to process. must be of type {@link CreditCard}
     * @param paymentRequestDTO payment details containing transaction details and cvc
     * @return PaymentResultResponse the result of the payment processing operation
     * @throws UnauthorizedAccessException if user has no access to the credit card
     */
    @Override
    public PaymentResultResponse process(PaymentMethod paymentMethod, PaymentRequestDTO paymentRequestDTO) {
        CreditCard cc = (CreditCard) paymentMethod;
        if (!permissionService.hasPaymentMethodAccess(paymentMethod)) {
            throw new UnauthorizedAccessException("Credit card does not belong to current user");
        }
        return service.paymentTest(cc, paymentRequestDTO.cvc());
    }
}
