package com.example.courier.payment;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;
import com.example.courier.domain.Payment;
import com.example.courier.domain.PaymentAttempt;
import com.example.courier.repository.PaymentAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentAttemptService {
    private final PaymentAttemptRepository repository;

    public PaymentAttemptService(PaymentAttemptRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentAttempt createAttempt(Payment payment) {
        PaymentAttempt attempt = new PaymentAttempt(PaymentAttemptStatus.PENDING, ProviderType.UNKNOWN, "");
        attempt.setPayment(payment);

        return repository.saveAndFlush(attempt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAttempt(PaymentAttempt attempt, PaymentAttemptStatus status, ProviderType provider, String txId, String failureReason) {
        attempt.setStatus(status);
        attempt.setProvider(provider);
        attempt.setTransactionId(txId);
        attempt.setFailureReason(failureReason);

        repository.saveAndFlush(attempt);
    }
}
