package com.example.courier.dto;

import com.example.courier.common.PaymentStatus;

import java.math.BigDecimal;

public record PaymentDTO(OrderDTO orderDTO, Long paymentMethodId, PaymentMethodDTO newPaymentMethod,
                         BigDecimal amount, PaymentStatus status, String cvc) {
}
