package com.example.courier.dto;

import com.example.courier.domain.Payment;

import java.math.BigDecimal;

public record PaymentDetailsDTO(BigDecimal amount, String status) {
    public static PaymentDetailsDTO fromPayment(Payment payment) {
        return new PaymentDetailsDTO(payment.getAmount(), payment.getStatus().name());
    }
}
