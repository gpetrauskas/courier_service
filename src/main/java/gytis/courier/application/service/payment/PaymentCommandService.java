package gytis.courier.application.service.payment;

import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.command.PaymentSectionUpdateCommand;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.payment.CancelPaymentUseCase;
import gytis.courier.application.port.in.payment.CreatePaymentUseCase;
import gytis.courier.application.port.in.payment.PayUseCase;
import gytis.courier.application.port.in.payment.PaymentUpdateUseCase;
import gytis.courier.application.port.in.person.SaveNewPaymentMethodUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.paymentmethod.PaymentMethodCommandPort;
import gytis.courier.application.readmodel.payment.PayReadModel;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.domain.event.DomainEvent;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import gytis.courier.domain.payment.PaymentAttemptStatus;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentCommandService implements CreatePaymentUseCase, CancelPaymentUseCase, PayUseCase, PaymentUpdateUseCase {
    private final PaymentCommandPort paymentPort;
    private final Logger logger = LoggerFactory.getLogger(PaymentCommandService.class);
    private final PaymentMethodCommandPort methodCommandPort;
    private final PaymentAttemptStarter attemptStarter;
    private final SaveNewPaymentMethodUseCase newPaymentMethodUseCase;
    private final PaymentProcessorFactory processorFactory;
    private final DomainEventPublisher eventPublisher;
    private final PaymentMethodFactory methodFactory;
    private final ActivityLogUseCase logUseCase;

    public PaymentCommandService(PaymentCommandPort paymentPort, PaymentMethodCommandPort methodCommandPort, PaymentAttemptStarter attemptStarter, SaveNewPaymentMethodUseCase newPaymentMethodUseCase,
                                 PaymentProcessorFactory processorFactory, DomainEventPublisher eventPublisher, PaymentMethodFactory methodFactory, ActivityLogUseCase logUseCase) {
        this.paymentPort = paymentPort;
        this.methodCommandPort = methodCommandPort;
        this.attemptStarter = attemptStarter;
        this.newPaymentMethodUseCase = newPaymentMethodUseCase;
        this.processorFactory = processorFactory;
        this.eventPublisher = eventPublisher;
        this.methodFactory = methodFactory;
        this.logUseCase = logUseCase;
    }

    public void create(Long orderId, BigDecimal amount) {
        Payment payment = Payment.create(orderId, amount);
        paymentPort.create(payment);
    }

    @Override
    @Transactional
    public PayReadModel pay(PaymentCommand command) {
        PaymentMethod method;
        if (command.existingMethodId() != null) {
            method = methodCommandPort.findByIdAndUserId(command.existingMethodId(), command.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
        } else {
            method = methodFactory.from(command.command());
        }

        Payment payment = attemptStarter.start(command.orderId(), method.providerType());
        PaymentResult result = processorFactory.getProcessor(method).process(method, command.cvc());

        var maybeEvent = completeAttempt(payment, result);

        saveCompletedAttempt(payment, result, command.orderId());
        maybeEvent.ifPresent(eventPublisher::publish);

        maybeSaveNewMethod(command, method, result);

        if (result.success()) {
            logUseCase.saveLog("USER", "payment succeeded", "Payment #" + payment.getId() + " amount: " + payment.getAmount() + " succeed using " + result.providerType().name());
        } else {
            logUseCase.saveLog("USER", "payment failed", "Payment #" + payment.getId() + "failed - " + result.failureReason());
        }

        return new PayReadModel(
                result.providerType().name(),
                result.transactionId(),
                result.success(),
                result.failureReason(),
                result.savedMethod()
        );
    }

    @Override
    public void update(Long orderId, PaymentSectionUpdateCommand command) {
        Payment payment = paymentPort.findByOrderId(orderId).orElseThrow();
        payment.changeStatus(command.status());

        paymentPort.update(payment);
    }

    @Override
    @Transactional
    public void cancelByOrderId(Long orderId) {
        Payment payment = paymentPort.findByOrderIdLocked(orderId).orElseThrow();
        payment.cancel();
        paymentPort.update(payment);
    }

    private Optional<DomainEvent> completeAttempt(Payment payment, PaymentResult result) {
        try {
            PaymentAttempt attempt = payment.getPaymentAttempts().stream()
                    .filter(pa -> pa.getStatus().equals(PaymentAttemptStatus.PENDING))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
            try {
                return payment.completeAttempt(attempt, result);
            } catch (Exception e) {
                if (result.success()) {
                    attempt.markSuccess(result.transactionId());
                } else {
                    attempt.markFailure(result.failureReason());
                }

                paymentPort.update(payment);
                throw e;
            }
        } catch (Exception e) {
            logger.error("CRITICAL: charge result could not be saved. Payment #{}, orderId {}, provider {}, transactionId {}, success {} ",
                    payment.getId(), payment.getOrderId(), result.providerType(), result.transactionId(), result.success(), e);
            throw e;
        }
    }

    private void saveCompletedAttempt(Payment payment, PaymentResult result, Long orderId) {
        try {
            paymentPort.update(payment);
        } catch (Exception e) {
            String message = "CRITICAL: payment save failed after charge. Payment #" + payment.getId() +
                    " orderId: " + orderId +
                    " provider: " + result.providerType() +
                    " transactionId: " + result.transactionId() +
                    " success: " + result.success();
            logger.error(message, e);
            throw e;
        }
    }

    private void maybeSaveNewMethod(PaymentCommand command, PaymentMethod method, PaymentResult result) {
        boolean isNewMethod = method.getId() == null;
        if (isNewMethod && result.savedMethod() && result.success() && result.token() != null) {
            try {
                newPaymentMethodUseCase.save(command.userId(), method, result.token());
            } catch (Exception e) {
                logUseCase.saveLog("SYSTEM", "Payment method save", "Payment method save failed...");
            }
        }
    }
}