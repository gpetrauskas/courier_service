package com.example.courier.payment.handler;

import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.CreditCard;
import com.example.courier.domain.Payment;
import com.example.courier.domain.PaymentMethod;
import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.UnauthorizedPaymentMethodException;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExistingPaymentMethodHandler implements PaymentHandler {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CreditCardService creditCardService;

    @Override
    public boolean isSupported(PaymentRequestDTO paymentRequestDTO) {
        return paymentRequestDTO.paymentMethodId() != null;
    }

    @Override
    public ResponseEntity<String> handle(PaymentRequestDTO paymentRequestDTO, Payment payment) {
        Long paymentMethodId = paymentRequestDTO.paymentMethodId();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new PaymentMethodNotFoundException("Payment method not found"));
        if (!paymentMethod.getUser().equals(payment.getOrder().getUser())) {
            throw new UnauthorizedPaymentMethodException("Unauthorized payment method.");
        }

        if (paymentMethod instanceof CreditCard) {
            CreditCard card = (CreditCard) paymentMethod;
            ResponseEntity<String> responseEntity = creditCardService.paymentTest(card, paymentRequestDTO.cvc());
            if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                payment.setStatus(PaymentStatus.FAILED);
                throw new PaymentFailedException(responseEntity.getBody());
            }
        }

        payment.setPaymentMethod(paymentMethod);
        return ResponseEntity.ok("Payment made successfully.");
    }
}
