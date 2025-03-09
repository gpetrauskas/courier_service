package com.example.courier.dto.response;

import java.math.BigDecimal;

public record AdminPaymentResponseDTO(
        Long id, BigDecimal amount, String status
) {
}
