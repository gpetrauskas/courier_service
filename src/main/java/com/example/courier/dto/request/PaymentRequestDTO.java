package com.example.courier.dto.request;

import com.example.courier.common.PaymentStatus;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaymentMethodDTO;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        Long paymentMethodId,
        PaymentMethodDTO newPaymentMethod,
        String cvc
) {
}
