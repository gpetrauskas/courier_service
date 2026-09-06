package gytis.courier.application.service.payment;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.ProviderType;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class PaymentAttemptStarter {
    private final ActivityLogUseCase logUseCase;
    private final PaymentCommandPort port;

    public PaymentAttemptStarter(ActivityLogUseCase logUseCase, PaymentCommandPort port) {
        this.logUseCase = logUseCase;
        this.port = port;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment start(Long orderId, ProviderType providerType) {
        Payment payment = port.findByOrderIdLocked(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        System.out.println("Thread " + Thread.currentThread().getName() + " " + payment.getId() + " " + payment.getStatus());

        payment.startAttempt(providerType);
        System.out.println("Thread " + Thread.currentThread().getName() + " " + payment.getId() + " " + payment.getStatus());
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());

        logUseCase.saveLog("USER", "pay", "Payment attempt started for order#" + orderId);
        return port.update(payment);
    }
}
