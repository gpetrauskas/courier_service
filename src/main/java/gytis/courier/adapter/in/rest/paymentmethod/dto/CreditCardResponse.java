package gytis.courier.adapter.in.rest.paymentmethod.dto;


public record CreditCardResponse(
        Long id,
        String providerType,
        boolean saved,
        String last4
) implements PaymentMethodResponse {
}
