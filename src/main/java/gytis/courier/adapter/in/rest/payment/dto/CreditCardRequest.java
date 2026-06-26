package gytis.courier.adapter.in.rest.payment.dto;

public record CreditCardRequest(
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        boolean saveCard
) implements PaymentMethodRequest {
}
