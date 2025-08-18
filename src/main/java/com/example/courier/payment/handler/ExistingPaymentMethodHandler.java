package com.example.courier.payment.handler;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.payment.method.PaymentMethodService;
import com.example.courier.service.permission.PermissionService;
import com.example.courier.service.security.CurrentPersonService;
import org.springframework.stereotype.Component;

@Component
public class ExistingPaymentMethodHandler implements PaymentHandler {

    private final PaymentMethodService paymentMethodService;
    private final PaymentProcessorRegistry processorRegistry;
    private final CurrentPersonService currentPersonService;
    private final PermissionService permissionService;

    public ExistingPaymentMethodHandler(PaymentMethodService paymentMethodService,
                                        PaymentProcessorRegistry processorRegistry,
                                        CurrentPersonService currentPersonService,
                                        PermissionService permissionService) {
        this.paymentMethodService = paymentMethodService;
        this.processorRegistry = processorRegistry;
        this.currentPersonService = currentPersonService;
        this.permissionService = permissionService;

    }

    /*
    * Checks if dto has saved method ID
    * returns true if .paymentMethodId() is not null
    */
    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.paymentMethodId() != null;
    }

    /*
    * Finds saved payment method by ID
    * delegates to right processor
    */
    @Override
    public PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO) {
        PaymentMethod paymentMethod = paymentMethodService.fetchPaymentMethodById(paymentRequestDTO.paymentMethodId());
        if (!permissionService.hasPaymentMethodAccess(currentPersonService.getCurrentPerson(), paymentMethod)) {
            throw new UnauthorizedAccessException("Payment method does not belong to current user");
        }

        return processorRegistry
                .getProcessor(paymentMethod)
                .process(paymentMethod, paymentRequestDTO);
    }
}
