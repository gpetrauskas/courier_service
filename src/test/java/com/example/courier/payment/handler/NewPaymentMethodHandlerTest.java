package com.example.courier.payment.handler;

import com.example.courier.common.ProviderType;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PayPalDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.factory.PaymentMethodFactory;
import com.example.courier.payment.processor.PaymentProcessor;
import com.example.courier.payment.processor.PaymentProcessorRegistry;
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
public class NewPaymentMethodHandlerTest {

    @Mock
    PaymentProcessorRegistry processorRegistry;
    @Mock
    PaymentMethodFactory paymentMethodFactory;

    @InjectMocks NewPaymentMethodHandler newPaymentMethodHandler;

    private final BigDecimal amount = BigDecimal.valueOf(20);
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
    }

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
        CreditCardDTO ccDTO = new CreditCardDTO(null, "1111222233334444", "01/27", "user name", true);
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(ccDTO);

        CreditCard savedCC = new CreditCard();

        PaymentProcessor<CreditCard> mockProcessor = mock(PaymentProcessor.class);
        PaymentResultResponse expected = new PaymentResultResponse("success", "APPROVED", ProviderType.CREDIT_CARD, "txId_123");

        when(paymentMethodFactory.create(ccDTO, user, requestDTO.cvc())).thenReturn(savedCC);
        when(processorRegistry.getProcessor(savedCC)).thenReturn(mockProcessor);
        when(mockProcessor.process(savedCC, requestDTO, amount)).thenReturn(expected);

        PaymentResultResponse response = newPaymentMethodHandler.handle(requestDTO, user, amount);

        assertEquals(expected, response);
        verify(paymentMethodFactory).create(ccDTO, user, requestDTO.cvc());
        verify(processorRegistry).getProcessor(savedCC);
        verify(mockProcessor).process(savedCC, requestDTO, amount);
    }

    @Test
    void shouldThrowException_whenPaymentMethodIsPaypalDTO() {
        PaymentRequestDTO requestDTO = createPaymentRequestDTO(mock(PayPalDTO.class));

        when(paymentMethodFactory.create(requestDTO.newPaymentMethod(), user, "321"))
                .thenThrow(new UnsupportedOperationException("Paypal not supported yet"));

        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> newPaymentMethodHandler.handle(requestDTO, user, amount));

        assertEquals("Paypal not supported yet", ex.getMessage());
    }

    private PaymentRequestDTO createPaymentRequestDTO(PaymentMethodDTO methodDto) {
        return new PaymentRequestDTO(null, methodDto, "321");
    }
}
