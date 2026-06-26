package gytis.courier.application.service.payment;

import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.command.PaymentSectionUpdateCommand;
import gytis.courier.application.port.in.payment.CancelPaymentUseCase;
import gytis.courier.application.port.in.payment.CreatePaymentUseCase;
import gytis.courier.application.port.in.payment.PayUseCase;
import gytis.courier.application.port.in.payment.PaymentUpdateUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.application.readmodel.payment.PayReadModel;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.domain.person.User;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentCommandService implements CreatePaymentUseCase, CancelPaymentUseCase, PayUseCase, PaymentUpdateUseCase {
    private final PaymentCommandPort paymentPort;
    private final UserCommandPort userPort;
    private final PaymentProcessorFactory processorFactory;
    private final DomainEventPublisher eventPublisher;
    private final PaymentMethodFactory methodFactory;

    public PaymentCommandService(PaymentCommandPort paymentPort, UserCommandPort userPort,
                                 PaymentProcessorFactory processorFactory, DomainEventPublisher eventPublisher, PaymentMethodFactory methodFactory) {
        this.paymentPort = paymentPort;
        this.userPort = userPort;
        this.processorFactory = processorFactory;
        this.eventPublisher = eventPublisher;
        this.methodFactory = methodFactory;
    }

    public void create(Long orderId, BigDecimal amount) {
        Payment payment = Payment.create(orderId, amount);
        paymentPort.create(payment);
    }

    @Override
    @Transactional
    public PayReadModel pay(PaymentCommand command) {
        User user = userPort.findWithPaymentMethodsById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Payment payment = paymentPort.findByOrderId(command.orderId());

        PaymentMethod method = determinePaymentMethod(command, user);
        System.out.println("koks method " + method.providerType());
        PaymentAttempt attempt = payment.startAttempt(method.providerType());

        var result = processorFactory.getProcessor(method).process(method, command.cvc());
        var maybeEvent = payment.completeAttempt(attempt, result);

        boolean isNewMethod = method.getId() == null;
        if (isNewMethod && result.savedMethod() && result.success() && result.token() != null) {
            if (user.maybeSaveMethod(method, result.token())) {
                userPort.saveWithPaymentMethods(user);
            }
        }

        paymentPort.update(payment);
        maybeEvent.ifPresent(eventPublisher::publish);

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
        Payment payment = paymentPort.findByOrderId(orderId);
        payment.changeStatus(command.status());

        paymentPort.update(payment);
    }

    @Override
    public void cancelByOrderId(Long orderId) {
        Payment payment = paymentPort.findByOrderId(orderId);
        payment.cancel();
        paymentPort.update(payment);
    }

    private PaymentMethod determinePaymentMethod(PaymentCommand command, User user) {
        return (command.existingMethodId() != null)
                ? user.getMethodById(command.existingMethodId())
                : methodFactory.from(command.command());
    }
}