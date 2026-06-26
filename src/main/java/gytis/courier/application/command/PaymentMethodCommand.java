package gytis.courier.application.command;

public sealed interface PaymentMethodCommand permits CreditCardCommand, PaypalCommand {
    
}
