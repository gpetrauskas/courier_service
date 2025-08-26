package com.example.courier.dto;

import jakarta.validation.constraints.NotBlank;

public record CreditCardDTO(
        Long id,
        @NotBlank String cardNumber,
        @NotBlank String expiryDate,
        @NotBlank String cardHolderName,
        boolean saveCard
) implements PaymentMethodDTO {
}
