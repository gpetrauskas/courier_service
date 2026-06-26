package gytis.courier.application.port.in.payment;

import gytis.courier.application.command.PaymentCommand;
import gytis.courier.application.readmodel.payment.PayReadModel;

public interface PayUseCase {
    PayReadModel pay(PaymentCommand command);
}
