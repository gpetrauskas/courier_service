package com.example.courier.dto;

public record CreditCardDTO(String cardNumber, String expiryDate, String cardHolderName,
                            String cvc, boolean saveCard) implements PaymentMethodDTO {
}
