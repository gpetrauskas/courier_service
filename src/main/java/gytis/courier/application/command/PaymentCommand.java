package gytis.courier.application.command;

public record PaymentCommand(
        Long userId,
        Long orderId,
        Long existingMethodId,
        PaymentMethodCommand command,
        String cvc
) {
}
