package gytis.courier.application.readmodel.paymentmethod;

public record CreditCardReadModel(
        Long id, String type, boolean saved, String last4
) implements UserPaymentMethodReadModel {
}
