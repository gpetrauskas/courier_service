package gytis.courier.adapter.in.rest.payment.dto;

public record PaymentRequest(
        Long paymentMethodId,
        PaymentMethodRequest newPaymentMethod,
        String cvc
) {
}
