package com.example.courier.dto;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.Payment;

import java.math.BigDecimal;

public record AdminPaymentDTO(Long id, BigDecimal amount, PaymentStatus status) {

}
