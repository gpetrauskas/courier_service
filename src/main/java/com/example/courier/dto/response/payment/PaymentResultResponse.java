package com.example.courier.dto.response.payment;

import com.example.courier.common.ProviderType;

public record PaymentResultResponse(
        String status,
        String message,
        ProviderType provider,
        String transactionId
) {
}
