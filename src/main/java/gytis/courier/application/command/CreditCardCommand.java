package gytis.courier.application.command;

public record CreditCardCommand(
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        boolean saveCard
) implements PaymentMethodCommand {
}
