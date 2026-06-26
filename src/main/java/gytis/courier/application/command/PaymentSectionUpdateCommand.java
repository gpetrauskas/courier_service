package gytis.courier.application.command;

import gytis.courier.domain.payment.PaymentStatus;

public record PaymentSectionUpdateCommand(
        PaymentStatus status
) {
}
