package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentProcessorRegistry {

    private final List<PaymentProcessor> processors;

    public PaymentProcessorRegistry(List<PaymentProcessor> processors) {
        this.processors = processors;
    }

    public PaymentProcessor getProcessor(PaymentMethod method) {
        return processors.stream()
                .filter(m -> m.supports(method))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No processor found for method type: " + method.getClass().getSimpleName())
                );
    }
}
