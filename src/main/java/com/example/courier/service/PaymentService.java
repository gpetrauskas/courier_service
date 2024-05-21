package com.example.courier.service;

import com.example.courier.domain.*;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.exception.PaymentFailedException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.UnauthorizedPaymentMethodException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CreditCardService creditCardService;

    public ResponseEntity<String> processPayment(PaymentDTO paymentDTO, Payment payment) {
        Order order = orderRepository.findById(payment.getOrder().getId()).orElseThrow(() ->
                new RuntimeException("Order not found."));
        User user = userRepository.findById(payment.getOrder().getUser().getId()).orElseThrow(() ->
                new RuntimeException("user not found"));
        if (payment.getStatus().equals("PAID")) {
            throw new RuntimeException("No active payment for this order.");
        }

        try {
            if (paymentDTO.newPaymentMethod() != null) {
                handleNewPaymentMethod(paymentDTO, payment, user);
            } else if (paymentDTO.paymentMethodId() != null) {
                handleExistingPaymentMethod(paymentDTO, payment);
            }

            payment.setStatus("PAID");
            order.setStatus("CONFIRMED");
            order.getPackageDetails().setStatus("PICKING_UP");

            orderRepository.save(order);
            paymentRepository.save(payment);


            return ResponseEntity.ok("Payment made successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during payment. " + e.getMessage());
        }
    }

    private void handleNewPaymentMethod(PaymentDTO paymentDTO, Payment payment, User user) {
        PaymentMethodDTO paymentMethodDTO = paymentDTO.newPaymentMethod();
        if (paymentMethodDTO instanceof CreditCardDTO) {
            CreditCard card = creditCardService.setupCreditCard((CreditCardDTO) paymentMethodDTO, user);

            ResponseEntity<String> responseEntity = creditCardService.paymentTest(card, paymentDTO.cvc());
            if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                throw new PaymentFailedException(responseEntity.getBody());
            }

            paymentMethodRepository.saveAndFlush(card);
            payment.setPaymentMethod(card);
        }
    }

    private void handleExistingPaymentMethod(PaymentDTO paymentDTO, Payment payment) {
        Long paymentMethodId = paymentDTO.paymentMethodId();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElseThrow(() ->
                new PaymentMethodNotFoundException("Payment method not found."));
        if (!paymentMethod.getUser().equals(payment.getOrder().getUser())) {
            throw new UnauthorizedPaymentMethodException("Payment was not found");
        }

        if (paymentMethod instanceof CreditCard) {
            CreditCard card = (CreditCard) paymentMethod;

            ResponseEntity<String> responseEntity = creditCardService.paymentTest(card, paymentDTO.cvc());
            if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                throw new PaymentFailedException(responseEntity.getBody());
            }
        }

        payment.setPaymentMethod(paymentMethod);
    }
}
