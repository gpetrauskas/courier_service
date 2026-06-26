package gytis.courier.application.readmodel.paymentmethod;

public sealed interface UserPaymentMethodReadModel permits CreditCardReadModel, PaypalReadModel {
    Long id();
    String type();
    boolean saved();
}
