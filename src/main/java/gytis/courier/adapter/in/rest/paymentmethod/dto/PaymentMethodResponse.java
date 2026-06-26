package gytis.courier.adapter.in.rest.paymentmethod.dto;

public interface PaymentMethodResponse {
    Long id();
    String providerType();
    boolean saved();
}
