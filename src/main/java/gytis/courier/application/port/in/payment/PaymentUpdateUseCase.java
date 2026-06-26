package gytis.courier.application.port.in.payment;

import gytis.courier.application.command.PaymentSectionUpdateCommand;

public interface PaymentUpdateUseCase {
    void update(Long orderId, PaymentSectionUpdateCommand command);
}
