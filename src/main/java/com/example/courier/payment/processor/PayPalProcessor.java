package com.example.courier.payment.processor;

import com.example.courier.domain.PayPal;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import org.springframework.stereotype.Component;

@Component
public class PayPalProcessor implements PaymentProcessor {
    @Override
    public boolean supports(PaymentMethod paymentMethod) {
       return paymentMethod instanceof PayPal;
    }

    @Override
    public PaymentResultResponse process(PaymentMethod paymentMethod, PaymentRequestDTO paymentRequestDTO) {
        PayPal payPal = (PayPal) paymentMethod;

        return null;
    }
}
