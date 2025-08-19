package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentProcessorRegistry {

    private final List<PaymentProcessor> processors;

    public PaymentProcessorRegistry(List<PaymentProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Finds the appropriate processor for the given payment method
     *
     * Iterates through all available processors and return the first one that
     * supports the specified payment method type
     *
     * @param method the payment method to find a processor for
     * @return the supporting PaymentProcessor implementation
     * @throws ResourceNotFoundException if no processor is found that supports given payment method type
     */
    public PaymentProcessor getProcessor(PaymentMethod method) {
        return processors.stream()
                .filter(m -> m.supports(method))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No processor found for method type: " + method.getClass().getSimpleName())
                );
    }
}
