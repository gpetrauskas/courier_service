package com.example.courier.payment.handler;

import com.example.courier.domain.Payment;
import com.example.courier.dto.PaymentDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentHandler {
    boolean isSupported(PaymentDTO paymentDTO);
    ResponseEntity<String> handle(PaymentDTO paymentDTO, Payment payment);
}
