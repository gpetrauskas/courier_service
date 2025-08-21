package com.example.courier.payment.paymentservicetest;

import com.example.courier.common.*;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.Payment;
import com.example.courier.domain.PaymentAttempt;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.exception.PaymentHandlerNotFoundException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.payment.PaymentAttemptService;
import com.example.courier.payment.PaymentService;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.service.security.CurrentPersonService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessPaymentTest {

    @Mock
    CurrentPersonService currentPersonService;
    @Mock
    PaymentRepository repository;
    @Mock
    PaymentAttemptService paymentAttemptService;
    @Mock
    List<PaymentHandler> handlers;

    @InjectMocks
    PaymentService service;

    private final Long ORDER_ID = 2L;
    private final Long USER_ID = 1L;

    private Payment payment;
    private PaymentRequestDTO paymentRequestDTO;
    private PaymentAttempt paymentAttempt;

    @BeforeEach
    void setup() {
        payment = createMockPayment();
        paymentAttempt = createPaymentAttempt();
        PaymentMethodDTO methodDTO = new CreditCardDTO(null, "1111222233334444", "01/27", "Frodo Baggins", null, false);
        paymentRequestDTO = new PaymentRequestDTO(99L, methodDTO, "321");
    }

    @Test
    void shouldSuccessfullyProcessPaymentAndReturnResponse() {
        PaymentHandler handler = setupCommonHappyPath();
        PaymentResultResponse resultResponse = new PaymentResultResponse("success", "APPROVED", ProviderType.CREDIT_CARD, "txid_123");

        when(handler.handle(paymentRequestDTO)).thenReturn(resultResponse);

        var response = service.processPayment(paymentRequestDTO, ORDER_ID);

        assertEquals("success", response.status());
        verify(paymentAttemptService).updateAttempt(paymentAttempt, PaymentAttemptStatus.SUCCESS, ProviderType.CREDIT_CARD,"txid_123", "");
    }

    @Test
    void shouldThrow_whenHandleMethodFails() {
        PaymentHandler handler = setupUntilHandlerSElection();

        when(paymentAttemptService.createAttempt(payment)).thenReturn(paymentAttempt);
        when(handler.isSupported(paymentRequestDTO)).thenReturn(true);
        when(handler.handle(paymentRequestDTO)).thenThrow(new PaymentFailedException(
                "fail", ProviderType.CREDIT_CARD, PaymentAttemptStatus.FAILED, ""));

        assertThrows(PaymentFailedException.class, () -> service.processPayment(paymentRequestDTO, ORDER_ID));

        verify(paymentAttemptService).updateAttempt(paymentAttempt, PaymentAttemptStatus.FAILED, ProviderType.CREDIT_CARD, "", "fail");
    }

    @Test
    void shouldThrow_whenHandlerNotFound() {
        PaymentHandler handler = setupUntilHandlerSElection();

        when(handler.isSupported(paymentRequestDTO)).thenReturn(false);

        assertThrows(PaymentHandlerNotFoundException.class,
                () -> service.processPayment(paymentRequestDTO, ORDER_ID));
    }

    @Test
    void shouldThrow_whenPaymentNotFound() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(USER_ID);
        when(repository.findByOrderIdAndUserId(ORDER_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.processPayment(paymentRequestDTO, ORDER_ID));
    }

    @Test
    void shouldThrow_whenPaymentAlreadyPaid() {
        payment.setStatus(PaymentStatus.PAID);

        when(currentPersonService.getCurrentPersonId()).thenReturn(USER_ID);
        when(repository.findByOrderIdAndUserId(ORDER_ID, USER_ID)).thenReturn(Optional.of(payment));

        assertThrows(ValidationException.class, () -> service.processPayment(paymentRequestDTO, ORDER_ID));

        verify(paymentAttemptService, never()).createAttempt(payment);
        verify(paymentAttemptService, never()).updateAttempt(any(), any(), any(), any(), any());
    }


    /*
    * helpers
    */
    private Payment createMockPayment() {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.PENDING);

        Parcel parcel = new Parcel();
        parcel.setId(3L);
        parcel.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);

        Payment payment = new Payment();
        payment.setId(4L);
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setAmount(BigDecimal.valueOf(20));

        order.setParcelDetails(parcel);
        payment.setOrder(order);

        return payment;
    }

    private PaymentAttempt createPaymentAttempt() {
        return new PaymentAttempt(PaymentAttemptStatus.PENDING, ProviderType.UNKNOWN, "");
    }

    private PaymentHandler setupCommonHappyPath() {
        PaymentHandler handler = setupUntilHandlerSElection();

        when(handler.isSupported(paymentRequestDTO)).thenReturn(true);
        when(paymentAttemptService.createAttempt(payment)).thenReturn(paymentAttempt);

        return handler;
    }

    private PaymentHandler setupUntilHandlerSElection() {
        PaymentHandler handler = mock(PaymentHandler.class);

        when(currentPersonService.getCurrentPersonId()).thenReturn(USER_ID);
        when(repository.findByOrderIdAndUserId(ORDER_ID, USER_ID)).thenReturn(Optional.of(payment));
        when(handlers.stream()).thenReturn(Stream.of(handler));

        return handler;
    }
}
