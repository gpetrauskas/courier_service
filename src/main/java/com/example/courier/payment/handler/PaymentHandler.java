package com.example.courier.payment.handler;

import com.example.courier.domain.Payment;
import com.example.courier.dto.request.PaymentRequestDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentHandler {
    boolean isSupported(PaymentRequestDTO paymentRequestDTO);
    ResponseEntity<String> handle(PaymentRequestDTO paymentRequestDTO, Payment payment);
}
