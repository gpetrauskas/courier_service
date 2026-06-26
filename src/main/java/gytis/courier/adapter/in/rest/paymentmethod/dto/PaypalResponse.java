package gytis.courier.adapter.in.rest.paymentmethod.dto;

public record PaypalResponse(
        Long id,
        String providerType,
        boolean saved,
        String ppEmail
) implements PaymentMethodResponse {
}
