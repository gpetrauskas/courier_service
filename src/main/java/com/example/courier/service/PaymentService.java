package com.example.courier.service;

import com.example.courier.domain.*;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.PaymentDTO;
import com.example.courier.dto.PaymentMethodDTO;
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

    public ResponseEntity<?> processPayment(PaymentDTO paymentDTO, Payment payment) {
        Order order = orderRepository.findById(payment.getOrder().getId()).orElseThrow(() ->
                new RuntimeException("Order not found."));

        try {
            System.out.println("1");
            if (paymentDTO.newPaymentMethod() != null) {
                PaymentMethodDTO paymentMethodDTO = paymentDTO.newPaymentMethod();
                System.out.println("2");
                if (paymentMethodDTO instanceof CreditCardDTO) {
                    CreditCardDTO creditCard = (CreditCardDTO) paymentMethodDTO;

                    CreditCard card = new CreditCard();
                    card.setUser(payment.getOrder().getUser());
                    card.setCardHolderName(creditCard.cardHolderName());
                    card.setCardNumber(creditCard.cardNumber());
                    card.setExpiryDate(creditCard.expiryDate());

                    System.out.println("3");
                    paymentMethodRepository.saveAndFlush(card);
                    payment.setPaymentMethod(card);
                }
            } else if (paymentDTO.paymentMethodId() != null) {
                PaymentMethod pm = paymentMethodRepository.findById(paymentDTO.paymentMethodId())
                        .orElseThrow(() -> new RuntimeException("Payment method not found."));
                if (!pm.getUser().equals(payment.getOrder().getUser())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment was nor found.");
                } else {
                    Long paymentMethodId = paymentDTO.paymentMethodId();
                    System.out.println("4");
                    PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElseThrow(() ->
                            new RuntimeException("Payment method not found."));
                    payment.setPaymentMethod(paymentMethod);
                }
            }

            System.out.println("5");
            payment.setStatus("PAID");
            order.setStatus("CONFIRMED");
            order.getPackageDetails().setStatus("PICKING_UP");

            orderRepository.save(order);
            paymentRepository.save(payment);


            return ResponseEntity.ok("Payment made successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during payment.");
        }
    }
}
