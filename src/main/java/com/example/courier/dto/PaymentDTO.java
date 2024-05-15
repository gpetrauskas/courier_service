package com.example.courier.dto;

import com.example.courier.domain.Order;

import java.math.BigDecimal;

public record PaymentDTO(Order order, Long paymentMethodId, PaymentMethodDTO newPaymentMethod,
                         BigDecimal amount, String status) {
}
