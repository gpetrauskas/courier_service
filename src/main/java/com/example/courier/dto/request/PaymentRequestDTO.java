package com.example.courier.dto.request;

import com.example.courier.common.PaymentStatus;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaymentMethodDTO;

import java.math.BigDecimal;

public record PaymentRequestDTO(OrderDTO orderDTO, Long paymentMethodId, PaymentMethodDTO newPaymentMethod,
                                BigDecimal amount, PaymentStatus status, String cvc) {
}
