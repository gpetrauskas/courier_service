package com.example.courier.payment.processor;

import com.example.courier.domain.CreditCard;
import com.example.courier.domain.PayPal;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.OneTimeCard;
import com.example.courier.exception.ResourceNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class PaymentProcessorRegistryTest {

    @Mock
    List<PaymentProcessor<? extends PaymentMethod>> processors;

    @InjectMocks
    PaymentProcessorRegistry registry;

    static Stream<PaymentMethod> paymentMethodStream() {
        return Stream.of(
                new CreditCard(),
                new OneTimeCard("1234123412341234", "12/27", "bilbo baggins"),
                new PayPal());
    }

    @ParameterizedTest
    @MethodSource("paymentMethodStream")
    void shouldReturnProcessor_whenSupportedProcessorExists(PaymentMethod method) {
        PaymentProcessor<PaymentMethod> mockProcessor = mock(PaymentProcessor.class);

        when(mockProcessor.supports(method)).thenReturn(true);
        when(processors.stream()).thenReturn(Stream.of(mockProcessor));

        PaymentProcessor<? extends PaymentMethod> result = registry.getProcessor(method);

        assertSame(mockProcessor, result);
        verify(mockProcessor).supports(method);
    }

    @ParameterizedTest
    @MethodSource("paymentMethodStream")
    void shouldThrow_whenNoSupportedMethodFound(PaymentMethod method) {
        when(processors.stream()).thenReturn(Stream.empty());
        assertThrows(ResourceNotFoundException.class, () -> registry.getProcessor(method));
    }
}
