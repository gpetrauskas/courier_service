package com.example.courier.dto;

import com.example.courier.domain.Order;

import java.math.BigDecimal;

public record PaymentDTO(Order order, String paymentMethod, BigDecimal amount, String status) {
}
