package com.example.courier.payment.processor;

import com.example.courier.domain.PayPal;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PayPalProcessor implements PaymentProcessor<PayPal> {
    @Override
    public boolean supports(PaymentMethod paymentMethod) {
       return paymentMethod instanceof PayPal;
    }

    @Override
    public PaymentResultResponse process(PayPal payPal, PaymentRequestDTO paymentRequestDTO, BigDecimal amount) {
        return null;
    }
}
