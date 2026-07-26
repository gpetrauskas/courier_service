package gytis.courier;

import gytis.courier.application.command.CreditCardCommand;
import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.person.SaveNewPaymentMethodUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.PaymentProcessorGateway;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.paymentmethod.PaymentMethodCommandPort;
import gytis.courier.application.readmodel.payment.PayReadModel;
import gytis.courier.application.result.PaymentResult;
import gytis.courier.application.service.payment.PaymentCommandService;
import gytis.courier.application.service.payment.PaymentMethodFactory;
import gytis.courier.application.service.payment.PaymentProcessorFactory;
import gytis.courier.domain.event.PaymentConfirmedEvent;
import gytis.courier.domain.payment.*;
import gytis.courier.domain.payment.method.CreditCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCommandServiceTest {
    private final PaymentCommand paymentCommand = new PaymentCommand(1L, 3L, null,
            new CreditCardCommand("1234000012340000", "me", "12/29", true), "111");
    private final PaymentCommand paymentCommandExisting = new PaymentCommand(1L, 3L, 4L,
            new CreditCardCommand(null, null, null, true), "111");

    @Mock
    private PaymentCommandPort paymentCommandPort;
    @Mock
    private PaymentProcessorFactory processorFactory;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private SaveNewPaymentMethodUseCase newPaymentMethodUseCase;
    @Mock
    private PaymentMethodCommandPort methodCommandPort;
    @Mock
    private PaymentMethodFactory paymentMethodFactory;
    @Mock
    private PaymentProcessorGateway paymentProcessorGateway;
    @Mock
    private ActivityLogUseCase logUseCase;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @Test
    void successPay_existingMethod() {
        Payment payment = Payment.restore(2L, 3L, BigDecimal.valueOf(21), PaymentStatus.NOT_PAID, List.of());
        CreditCard ccMethod = CreditCard.recover(4L, true, "tok_123123", "1234", "12/29", "me");
        PaymentResult result = new PaymentResult(ProviderType.CREDIT_CARD, "tx_id_123", true, null, true, "tok_321");

        when(paymentCommandPort.findByOrderId(paymentCommandExisting.orderId())).thenReturn(Optional.of(payment));
        when(methodCommandPort.findByIdAndUserId(paymentCommandExisting.existingMethodId(), paymentCommandExisting.userId())).thenReturn(Optional.of(ccMethod));
        when(processorFactory.getProcessor(ccMethod)).thenReturn(paymentProcessorGateway);
        when(paymentProcessorGateway.process(ccMethod, paymentCommandExisting.cvc())).thenReturn(result);

        PayReadModel payReadModel = paymentCommandService.pay(paymentCommandExisting);
        assertNotNull(payReadModel);

        verify(paymentCommandPort).update(payment);;
        verify(eventPublisher).publish(any(PaymentConfirmedEvent.class));
        verify(methodCommandPort).findByIdAndUserId(paymentCommandExisting.existingMethodId(), paymentCommandExisting.userId());
        verify(newPaymentMethodUseCase, never()).save(anyLong(), any(), anyString());

        assertEquals(result.transactionId(), payReadModel.transactionId());
    }

    @Test
    void successPay_newMethod() {
        Payment payment = Payment.restore(2L, 3L, BigDecimal.valueOf(21), PaymentStatus.NOT_PAID, List.of());
        CreditCard ccMethod = CreditCard.recover(null, true, "tok_123123", "1234", "12/29", "me");
        PaymentResult result = new PaymentResult(ProviderType.CREDIT_CARD, "tx_id_123", true, null, true, "tok_321");

        when(paymentCommandPort.findByOrderId(paymentCommand.orderId())).thenReturn(Optional.of(payment));
        when(paymentMethodFactory.from(paymentCommand.command())).thenReturn(ccMethod);
        when(processorFactory.getProcessor(ccMethod)).thenReturn(paymentProcessorGateway);
        when(paymentProcessorGateway.process(ccMethod, paymentCommand.cvc())).thenReturn(result);

        PayReadModel payReadModel = paymentCommandService.pay(paymentCommand);
        assertNotNull(payReadModel);

        verify(paymentCommandPort).update(payment);;
        verify(eventPublisher).publish(any(PaymentConfirmedEvent.class));
        verify(newPaymentMethodUseCase).save(paymentCommand.userId(), ccMethod, result.token());

        assertEquals(result.transactionId(), payReadModel.transactionId());
    }
}
