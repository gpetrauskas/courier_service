package gytis.courier;

import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import gytis.courier.domain.payment.*;
import gytis.courier.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    private Payment payment;

    @BeforeEach
    void setUp() {
        Long paymentId = 1L;
        Long orderId = 2L;
        BigDecimal amountToPay = BigDecimal.valueOf(90);
        List<PaymentAttempt> attempts = new ArrayList<>();

        payment = Payment.restore(paymentId, orderId, amountToPay, PaymentStatus.NOT_PAID, attempts);
    }

    @Test
    void successOnInitialAttemptCreation() {
        PaymentAttempt attempt = payment.startAttempt(ProviderType.CREDIT_CARD);

        assertEquals(PaymentAttemptStatus.PENDING, attempt.getStatus());
        assertEquals("", attempt.getTransactionId());
        assertEquals(1, payment.getPaymentAttempts().size());
    }

    @Test
    void throwOnInitialAttemptCreation() {
        payment.changeStatus(PaymentStatus.PAID);

        assertThrows(IllegalStateException.class, () -> payment.startAttempt(ProviderType.CREDIT_CARD));

        assertTrue(payment.getPaymentAttempts().isEmpty());
    }

    @Test
    void successOnCompleteAttempt() {
        PaymentAttempt attempt = payment.startAttempt(ProviderType.CREDIT_CARD);
        PaymentResult result = new PaymentResult(attempt.getProvider(), "tx_id_123", true, "", true, "token_123");

        Optional<PaymentConfirmedEvent> event = payment.completeAttempt(attempt, result);

        assertEquals(PaymentAttemptStatus.SUCCESS, attempt.getStatus());
        assertTrue(event.isPresent());
    }

    @Test
    void throwOnStatusChangeStatusInFinalState() {
        payment.changeStatus(PaymentStatus.CANCELED);

        assertThrows(InvalidStateTransitionException.class, () -> payment.changeStatus(PaymentStatus.PAID));
    }
}
