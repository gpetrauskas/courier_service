package com.example.courier.dto;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Payment;

import java.math.BigDecimal;

public record AdminPaymentDTO(Long id, BigDecimal amount, PaymentStatus status) {

    public static AdminPaymentDTO fromPayment(Payment payment) {
        return new AdminPaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
