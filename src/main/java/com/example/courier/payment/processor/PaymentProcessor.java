package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;

public interface PaymentProcessor {
    boolean supports(PaymentMethod paymentMethod);
    PaymentResultResponse process(PaymentMethod paymentMethod, PaymentRequestDTO paymentRequestDTO);
}
