package com.example.courier.payment.handler;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.Payment;
import com.example.courier.domain.User;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NewPaymentMethodHandler implements PaymentHandler {

    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.newPaymentMethod() != null;
    }

    @Override
    public ResponseEntity<String> handle(PaymentRequestDTO paymentRequestDTO, Payment payment) {
        User user = payment.getOrder().getUser();
        CreditCardDTO creditCardDTO = (CreditCardDTO) paymentRequestDTO.newPaymentMethod();
        CreditCard card = creditCardService.setupCreditCard(creditCardDTO, user);

        ResponseEntity<String> responseEntity = creditCardService.paymentTest(card, creditCardDTO.cvc());
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new PaymentFailedException(responseEntity.getBody());
        }

        if (!card.isSaved()) {
            paymentMethodRepository.saveAndFlush(creditCardService.dontSaveCreditCard(card));
        } else {
            paymentMethodRepository.saveAndFlush(card);
        }

        payment.setPaymentMethod(card);
        return ResponseEntity.ok("Payment done successfully");
    }
}
