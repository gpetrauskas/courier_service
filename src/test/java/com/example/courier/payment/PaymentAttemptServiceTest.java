package com.example.courier.payment;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;
import com.example.courier.domain.Payment;
import com.example.courier.domain.PaymentAttempt;
import com.example.courier.repository.PaymentAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentAttemptServiceTest {

    @Mock
    PaymentAttemptRepository repository;

    @InjectMocks
    PaymentAttemptService service;

    private PaymentAttempt paymentAttempt;

    @BeforeEach
    void setup() {
        paymentAttempt = new PaymentAttempt(PaymentAttemptStatus.PENDING, ProviderType.UNKNOWN, "");

    }

    @Test
    @DisplayName("createAttempt")
    void successfullyCreateInitialPaymentAttempt() {
        Payment payment = new Payment();
        paymentAttempt.setPayment(payment);

        when(repository.saveAndFlush(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        PaymentAttempt response = service.createAttempt(payment);

        assertEquals(PaymentAttemptStatus.PENDING, response.getStatus());
        assertEquals(ProviderType.UNKNOWN, response.getProvider());
        verify(repository).saveAndFlush(any());
    }

    @Test
    @DisplayName("update attempt - on payment success")
    void successfullyUpdatePaymentAttempt_whenPaymentSuccessfullyMade() {
        service.updateAttempt(paymentAttempt, PaymentAttemptStatus.SUCCESS, ProviderType.CREDIT_CARD, "txId_123", "");

        assertEquals(PaymentAttemptStatus.SUCCESS, paymentAttempt.getStatus());
        assertEquals(ProviderType.CREDIT_CARD, paymentAttempt.getProvider());
        assertEquals("txId_123", paymentAttempt.getTransactionId());
        assertEquals("", paymentAttempt.getFailureReason());
        verify(repository).saveAndFlush(paymentAttempt);
    }

    @Test
    @DisplayName("update attempt - on payment failure")
    void successfullyUpdatePayment_whenPaymentFailed() {
        service.updateAttempt(paymentAttempt, PaymentAttemptStatus.FAILED, ProviderType.CREDIT_CARD, "", "REJECTED");

        assertEquals(PaymentAttemptStatus.FAILED, paymentAttempt.getStatus());
        assertEquals(ProviderType.CREDIT_CARD, paymentAttempt.getProvider());
        assertEquals("", paymentAttempt.getTransactionId());
        assertEquals("REJECTED", paymentAttempt.getFailureReason());
        verify(repository).saveAndFlush(paymentAttempt);
    }
}
