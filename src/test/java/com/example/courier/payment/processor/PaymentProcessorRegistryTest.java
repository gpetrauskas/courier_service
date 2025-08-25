package com.example.courier.payment.processor;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorRegistryTest {

    @Mock
    List<PaymentProcessor> processors;

    @InjectMocks
    PaymentProcessorRegistry registry;

    @Test
    void shouldReturnProcessor_whenSupportedProcessorExists() {
        PaymentMethod cc = new CreditCard();
        PaymentProcessor mockProcessor = mock(PaymentProcessor.class);

        when(mockProcessor.supports(cc)).thenReturn(true);
        when(processors.stream()).thenReturn(Stream.of(mockProcessor));

        PaymentProcessor result = registry.getProcessor(cc);

        assertSame(mockProcessor, result);
        verify(mockProcessor).supports(cc);
    }

    @Test
    void shouldThrow_whenNoSupportedMethodFound() {
        assertThrows(ResourceNotFoundException.class, () -> registry.getProcessor(mock(PaymentMethod.class)));
    }
}
