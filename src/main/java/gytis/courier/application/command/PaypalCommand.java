package gytis.courier.application.command;

public record PaypalCommand(
        String ppEmail,
        boolean saved
) implements PaymentMethodCommand {
}
