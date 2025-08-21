package com.example.courier.payment.paymentservicetest;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.exception.PaymentCreationException;
import com.example.courier.payment.PaymentService;
import com.example.courier.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePaymentTests {

    @Mock
    private PaymentRepository repository;

    @InjectMocks
    PaymentService service;

    @Captor
    ArgumentCaptor<Payment> paymentArgumentCaptor;

    private BigDecimal amountToPay;
    private Order mockOrder;

    @BeforeEach
    void setup() {
        amountToPay = BigDecimal.valueOf(20);
        mockOrder = new Order();
        mockOrder.setId(1L);
    }

    @Test
    void shouldSuccessfullyCreatePayment_whenAllGood() {
        service.createPayment(mockOrder, amountToPay);

        verify(repository).save(paymentArgumentCaptor.capture());

        Payment savedPayment = paymentArgumentCaptor.getValue();

        assertEquals(mockOrder, savedPayment.getOrder());
        assertEquals(amountToPay, savedPayment.getAmount());
        assertEquals(PaymentStatus.NOT_PAID, savedPayment.getStatus());
    }

    @Test
    void shouldThrow_whenRepositoryFails() {
        when(repository.save(any(Payment.class))).thenThrow(new PaymentCreationException("DB error"));

        PaymentCreationException ex = assertThrows(PaymentCreationException.class,
                () -> service.createPayment(mockOrder, amountToPay));

        assertTrue(ex.getMessage().startsWith("Payment creation failure:"));
    }
}
