package com.example.courier.payment.handler;

import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.payment.method.PaymentMethodService;
import com.example.courier.payment.processor.PaymentProcessor;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.service.permission.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class ExistingPaymentMethodHandlerTest {

    @Mock
    PaymentMethodService paymentMethodService;
    @Mock
    PaymentProcessorRegistry processorRegistry;
    @Mock
    PermissionService permissionService;

    @InjectMocks
    ExistingPaymentMethodHandler existingPaymentMethodHandler;

    private final BigDecimal amount = BigDecimal.valueOf(20);
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
    }

    @Test
    void isSupported_shouldReturnTrue_whenMethodIsSupported() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(99L);

        boolean supported = existingPaymentMethodHandler.isSupported(requestDTO);

        assertTrue(supported);
    }

    @Test
    void isSupported_shouldReturnFalse_whenMethoIdIsNotFound() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(null);

        boolean isSupported = existingPaymentMethodHandler.isSupported(requestDTO);

        assertFalse(isSupported);
    }

    @Test
    void handle_shouldSuccessfullyHandleAndReturnPaymentResultResponse() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(99L);
        CreditCard savedMethod = new CreditCard();
        PaymentResultResponse expectedResponse = new PaymentResultResponse("success", "APPROVED", ProviderType.CREDIT_CARD, "txId_123");
        PaymentProcessor<CreditCard> mockProcessor = mock(PaymentProcessor.class);

        when(paymentMethodService.fetchPaymentMethodById(requestDTO.paymentMethodId())).
                thenReturn(savedMethod);
        when(permissionService.hasPaymentMethodAccess(savedMethod)).thenReturn(true);
        when(processorRegistry.getProcessor(savedMethod)).thenReturn(mockProcessor);
        when(mockProcessor.process(savedMethod, requestDTO, amount)).thenReturn(expectedResponse);

        var response = existingPaymentMethodHandler.handle(requestDTO, user, amount);

        assertEquals(expectedResponse, response);
        verify(paymentMethodService).fetchPaymentMethodById(99L);
        verify(permissionService).hasPaymentMethodAccess(savedMethod);
        verify(processorRegistry).getProcessor(savedMethod);
        verify(mockProcessor).process(savedMethod, requestDTO, amount);
    }

    @Test
    void handle_shouldThrow_whenPaymentMethodNameAndCurrentUserNameDoesNotMatch() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(99L);
        when(paymentMethodService.fetchPaymentMethodById(99L)).thenReturn(null);

        assertThrows(UnauthorizedAccessException.class,
                () -> existingPaymentMethodHandler.handle(requestDTO, user, amount));
    }

    private PaymentRequestDTO createPaymentRequestDTO(Long id) {
        return new PaymentRequestDTO(id, null, "321");
    }
}
