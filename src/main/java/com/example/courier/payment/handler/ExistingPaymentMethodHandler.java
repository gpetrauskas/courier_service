package com.example.courier.payment.handler;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.payment.method.PaymentMethodService;
import com.example.courier.service.permission.PermissionService;
import com.example.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ExistingPaymentMethodHandler implements PaymentHandler {

    private final PaymentMethodService paymentMethodService;
    private final PaymentProcessorRegistry processorRegistry;
    private final PermissionService permissionService;

    public ExistingPaymentMethodHandler(PaymentMethodService paymentMethodService,
                                        PaymentProcessorRegistry processorRegistry,
                                        PermissionService permissionService) {
        this.paymentMethodService = paymentMethodService;
        this.processorRegistry = processorRegistry;
        this.permissionService = permissionService;

    }

    /**
     * Determines if this handler supports the given request by checking
     * if payment method ID is given
     *
     * @param paymentRequestDTO incoming {@link PaymentRequestDTO} containing payment method details
     * @return true if paymentMethodId is not null, false if its null.
     */
    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.paymentMethodId() != null;
    }

    /**
     * Fetches {@link PaymentMethod} by given ID using paymentMethodService
     * and calls permissionService to validate if current user has access to it
     * Uses processorRegistry to find right processor and delegate paymentMethod to it
     *
     * @param paymentRequestDTO the {@link PaymentRequestDTO} containing payment method ID and payment details
     * @return PaymentResultResponse the result of payment processing
     * @throws ResourceNotFoundException if payment with given id not exists or processor was not found
     * @throws UnauthorizedAccessException if current user has no access to fetched payment
     */
    @Override
    public PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO) {
        PaymentMethod paymentMethod = paymentMethodService.fetchPaymentMethodById(paymentRequestDTO.paymentMethodId());
        if (!permissionService.hasPaymentMethodAccess(paymentMethod)) {
            throw new UnauthorizedAccessException("Payment method does not belong to current user");
        }

        return processorRegistry
                .getProcessor(paymentMethod)
                .process(paymentMethod, paymentRequestDTO);
    }
}
