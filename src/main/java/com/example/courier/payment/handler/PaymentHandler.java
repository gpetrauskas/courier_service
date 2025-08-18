package com.example.courier.payment.handler;

import com.example.courier.domain.Payment;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;

public interface PaymentHandler {
    boolean isSupported(PaymentRequestDTO paymentRequestDTO);
    PaymentResultResponse handle(PaymentRequestDTO paymentRequestDTO);
}
