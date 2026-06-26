package gytis.courier.application.readmodel.paymentmethod;

public record PaypalReadModel(
        Long id,
        String type,
        boolean saved,
        String ppEmail
) implements UserPaymentMethodReadModel {
}
