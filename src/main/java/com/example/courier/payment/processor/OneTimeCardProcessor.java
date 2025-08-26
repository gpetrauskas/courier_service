package com.example.courier.payment.processor;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.OneTimeCard;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.method.CreditCardService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OneTimeCardProcessor implements PaymentProcessor<OneTimeCard> {
    private final CreditCardService service;

    public OneTimeCardProcessor(CreditCardService service) {
        this.service = service;
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod instanceof OneTimeCard;
    }

    @Override
    public PaymentResultResponse process(OneTimeCard oneTimeCard, PaymentRequestDTO requestDTO, BigDecimal amount) {
        return service.chargeOneTimeCard(oneTimeCard, requestDTO.cvc(), amount);
    }
}
