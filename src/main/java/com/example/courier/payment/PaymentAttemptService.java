package com.example.courier.payment;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;
import com.example.courier.domain.Payment;
import com.example.courier.domain.PaymentAttempt;
import com.example.courier.repository.PaymentAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing {@link PaymentAttempt} entities
 *
 * Provides methods to create and update payment attempts with transactional guarantees
 */
@Service
public class PaymentAttemptService {
    private final PaymentAttemptRepository repository;

    public PaymentAttemptService(PaymentAttemptRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates new {@link PaymentAttempt} with status {@code PENDING}, provider {@code UNKNOWN},
     * and empty transactional ID, associated with given {@link Payment}
     *
     * This method starts a new transaction to ensure isolation from the callers context
     *
     * @param payment the payment entity associate with the attempt
     * @return the persisted {@link PaymentAttempt}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentAttempt createAttempt(Payment payment) {
        PaymentAttempt attempt = new PaymentAttempt(PaymentAttemptStatus.PENDING, ProviderType.UNKNOWN, "");
        attempt.setPayment(payment);

        return repository.saveAndFlush(attempt);
    }

    /**
     * Updates the given {@link PaymentAttempt} with the provided status, provider, transaction ID
     * and a failure reason(if it was a failure)
     *
     * This method starts a new transaction to ensure the update is commited independently
     *
     * @param attempt the payment attempt to update
     * @param status the new status of the payment attempt
     * @param provider the provider used
     * @param txId the transaction ID returned by the provider
     * @param failureReason explanation of failure, if it was a failure
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAttempt(PaymentAttempt attempt, PaymentAttemptStatus status, ProviderType provider, String txId, String failureReason) {
        attempt.setStatus(status);
        attempt.setProvider(provider);
        attempt.setTransactionId(txId);
        attempt.setFailureReason(failureReason);

        repository.saveAndFlush(attempt);
    }
}
