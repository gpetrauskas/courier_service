package com.example.courier.payment.handler;

import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.method.CreditCardService;
import com.example.courier.payment.processor.PaymentProcessor;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
import com.example.courier.payment.processor.PaymentProcessorRegistryTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewPaymentMethodHandlerTest {

    @Mock
    CreditCardService creditCardService;
    @Mock
    PaymentProcessorRegistry processorRegistry;

    @InjectMocks NewPaymentMethodHandler newPaymentMethodHandler;

    @Test
    void shouldReturnTrue_whenPaymentMethodIsNotNull() {
        PaymentRequestDTO dto = createPaymentRequestDTO(mock(CreditCardDTO.class));

        boolean supported = newPaymentMethodHandler.isSupported(dto);

        assertTrue(supported);
    }

    @Test
    void shouldReturnFalse_whenPaymentMethodIsNull(){
        PaymentRequestDTO dto = createPaymentRequestDTO(null);

        boolean supported = newPaymentMethodHandler.isSupported(dto);

        assertFalse(supported);
    }

    @Test
    void shouldSuccessfullyHandleCreditCardPayment_whenValidRequestGiven() {
        CreditCardDTO ccDTO = new CreditCardDTO(null, "1111222233334444", "01/27", "user name", "321", true);
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(ccDTO);

        CreditCard savedCC = new CreditCard();

        PaymentProcessor mockProcessor = mock(PaymentProcessor.class);
        PaymentResultResponse expected = new PaymentResultResponse("success", "APPROVED", ProviderType.CREDIT_CARD, "txId_123");

        when(creditCardService.setupCreditCard(ccDTO)).thenReturn(savedCC);
        when(processorRegistry.getProcessor(savedCC)).thenReturn(mockProcessor);
        when(mockProcessor.process(savedCC, requestDTO)).thenReturn(expected);

        PaymentResultResponse response = newPaymentMethodHandler.handle(requestDTO);

        assertEquals(expected, response);
        verify(creditCardService).setupCreditCard(ccDTO);
        verify(processorRegistry).getProcessor(savedCC);
        verify(mockProcessor).process(savedCC, requestDTO);
    }

    @Test
    void shouldThrowException_whenPaymentMethodIsPaypalDTO() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(mock(PayPalDTO.class));

        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> newPaymentMethodHandler.handle(requestDTO));

        assertEquals("Paypal not supported yet", ex.getMessage());
    }

    private PaymentRequestDTO createPaymentRequestDTO(PaymentMethodDTO methodDto) {
        return new PaymentRequestDTO(null, methodDto, "321");
    }
}
