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

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

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

    public ResponseEntity<String> processPayment(PaymentDTO paymentDTO, Payment payment) {
        Order order = orderRepository.findById(payment.getOrder().getId()).orElseThrow(() ->
                new RuntimeException("Order not found."));

        try {
            if (paymentDTO.newPaymentMethod() != null) {
                PaymentMethodDTO paymentMethodDTO = paymentDTO.newPaymentMethod();
                if (paymentMethodDTO instanceof CreditCardDTO) {
                    CreditCard card = setupCreditCard(paymentMethodDTO, payment);
                    String cvc = ((CreditCardDTO) paymentMethodDTO).cvc();

                    ResponseEntity<String> responseEntity = testingPayment(card, cvc);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        payment.setStatus("FAILED");
                        paymentRepository.save(payment);
                        System.out.println("3");
                        return responseEntity;
                    }

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
                    PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElseThrow(() ->
                            new RuntimeException("Payment method not found."));
                    if (paymentMethod instanceof CreditCard) {
                        CreditCard card = (CreditCard) paymentMethod;

                        ResponseEntity responseEntity = testingPayment(card, paymentDTO.cvc());
                        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                            payment.setStatus("FAILED");
                            paymentRepository.save(payment);
                            return responseEntity;
                        }
                    }
                    payment.setPaymentMethod(paymentMethod);
                }
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

    public CreditCard setupCreditCard(PaymentMethodDTO paymentMethodDTO, Payment payment) {
        CreditCardDTO creditCardDTO = (CreditCardDTO) paymentMethodDTO;

        CreditCard creditCard = new CreditCard();
        creditCard.setUser(payment.getOrder().getUser());
        creditCard.setCardHolderName(creditCardDTO.cardHolderName());
        creditCard.setCardNumber(creditCardDTO.cardNumber());
        creditCard.setExpiryDate(creditCardDTO.expiryDate());

        return creditCard;
    }

    public ResponseEntity<String> testingPayment(CreditCard card, String cvc) {
        if (cardExpired(card)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CARD EXPIRED");
        }
        if (card.getCardNumber().endsWith("00")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DECLINED");
        }
        if (cvc.endsWith("3")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("REJECTED");
        }
            return ResponseEntity.ok("APPROVED");
    }

    public boolean cardExpired(CreditCard card) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryDate = YearMonth.parse(card.getExpiryDate(), formatter);
        YearMonth currentDate = YearMonth.now();

        return expiryDate.isBefore(currentDate);
    }
}
