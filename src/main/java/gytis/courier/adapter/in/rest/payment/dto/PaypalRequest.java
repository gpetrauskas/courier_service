package gytis.courier.adapter.in.rest.payment.dto;

public record PaypalRequest(
        String ppEmail,
        boolean saved
) implements PaymentMethodRequest {
}
