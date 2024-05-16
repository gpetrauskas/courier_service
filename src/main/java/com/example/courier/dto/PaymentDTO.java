package com.example.courier.dto;

import java.math.BigDecimal;

public record PaymentDTO(OrderDTO orderDTO, Long paymentMethodId, PaymentMethodDTO newPaymentMethod,
                         BigDecimal amount, String status, String cvc) {
}
