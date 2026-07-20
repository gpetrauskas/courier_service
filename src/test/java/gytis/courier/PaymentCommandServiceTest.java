package gytis.courier;

import gytis.courier.application.command.CreditCardCommand;
import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.PaymentProcessorGateway;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.application.readmodel.payment.PayReadModel;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.application.service.payment.PaymentCommandService;
import gytis.courier.application.service.payment.PaymentMethodFactory;
import gytis.courier.application.service.payment.PaymentProcessorFactory;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import gytis.courier.domain.payment.*;
import gytis.courier.domain.payment.method.CreditCard;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.User;
import gytis.courier.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentCommandServiceTest {
    private final PaymentCommand paymentCommand = new PaymentCommand(1L, 3L, null,
            new CreditCardCommand("1234000012340000", "me", "12/29", true), "111");

    @Mock
    private PaymentCommandPort paymentCommandPort;
    @Mock
    private UserCommandPort userCommandPort;
    @Mock
    private PaymentProcessorFactory processorFactory;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private PaymentMethodFactory paymentMethodFactory;
    @Mock
    private PaymentProcessorGateway paymentProcessorGateway;
    @Mock
    private ActivityLogUseCase logUseCase;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @Test
    void successPay() {
        User user = new User(1L, "me", new Email("me@example.com"), "encodedPass");
        Payment payment = Payment.restore(2L, 3L, BigDecimal.valueOf(21), PaymentStatus.NOT_PAID, List.of());
        CreditCard ccMethod = CreditCard.recover(4L, true, "tok_123123", "1234", "12/29", "me");
        PaymentResult result = new PaymentResult(ProviderType.CREDIT_CARD, "tx_id_123", true, null, true, "tok_321");

        when(userCommandPort.findWithPaymentMethodsById(paymentCommand.userId())).thenReturn(Optional.of(user));
        when(paymentCommandPort.findByOrderId(paymentCommand.orderId())).thenReturn(payment);
        when(paymentMethodFactory.from(paymentCommand.command())).thenReturn(ccMethod);
        when(processorFactory.getProcessor(any())).thenReturn(paymentProcessorGateway);
        when(paymentProcessorGateway.process(any(), any())).thenReturn(result);

        PayReadModel payReadModel = paymentCommandService.pay(paymentCommand);
        assertNotNull(payReadModel);

        verify(paymentCommandPort).update(payment);
        verify(eventPublisher).publish(any(PaymentConfirmedEvent.class));

        assertEquals(result.transactionId(), payReadModel.transactionId());
    }

    @Test
    void throwsOnUserNotFound() {
        when(userCommandPort.findWithPaymentMethodsById(paymentCommand.userId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCommandService.pay(paymentCommand));
    }
}
