package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registry that resolves the correct {@link PaymentProcessor}
 * implementation for a given {@link PaymentMethod}
 *
 * uses {@code supports()} method of each processor to determine which
 * implementation can handle the specific method at runtime
 * */
@Component
public class PaymentProcessorRegistry {

    private final List<PaymentProcessor<? extends PaymentMethod>> processors;

    public PaymentProcessorRegistry(List<PaymentProcessor<? extends PaymentMethod>> processors) {
        this.processors = processors;
    }

    /**
     * Finds the appropriate processor that can handle provided payment method.
     *
     * This method iterates through all registered processors and return the first one that
     * supports the specified payment method type.
     *
     * The generic type parameter ensures that the returned processor is strongly typed
     * for the given {@link PaymentMethod} subtype.
     *
     * @param method the payment method to find a processor for
     * @return the supporting PaymentProcessor implementation
     * @throws ResourceNotFoundException if no processor supports the method
     */
    @SuppressWarnings("unchecked")
    public <T extends PaymentMethod> PaymentProcessor<T> getProcessor(T method) {
        return (PaymentProcessor<T>) processors.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No processor found"));
    }
}
