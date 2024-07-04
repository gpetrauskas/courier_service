package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.PaymentDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.exception.*;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private List<PaymentHandler> paymentHandlers;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public ResponseEntity<String> processPayment(PaymentDTO paymentDTO, Long orderId, Principal principal) {
        try {
            Payment payment = getPaymentByOrderId(orderId);
            Order order = payment.getOrder();

            if (!isPaymentValid(payment)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid payment founded.");
            }

            ResponseEntity<String> response = processPaymentHandler(paymentDTO, payment);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                handlePaymentSuccess(payment, order);
            } else {
                throw new PaymentFailedException("Payment handler failed.");
            }

            return response;

        } catch (PaymentFailedException e) {
            log.error("Error occurred during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during payment. " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during payment");
        }
    }

    private ResponseEntity<String> processPaymentHandler(PaymentDTO paymentDTO, Payment payment) {
        for (PaymentHandler handler : paymentHandlers) {
            if (handler.isSupported(paymentDTO)) {
                ResponseEntity<String> response = handler.handle(paymentDTO, payment);
                if (!response.getStatusCode().equals(HttpStatus.OK)) {
                    throw new PaymentFailedException(response.getBody());
                }
                return response;
            }
        }
        throw new PaymentFailedException("No handler for provided payment");
    }

    private void handlePaymentSuccess(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        order.getPackageDetails().setStatus(PackageStatus.PICKING_UP);
        savePayment(payment);
        log.info("Payment succeeded for order id {} and payment id {}", order.getId(), payment.getId());
    }

    private boolean isPaymentValid(Payment payment) {
        if (payment.getStatus().equals(PaymentStatus.PAID) || payment.getStatus().equals(PaymentStatus.CANCELED)) {
            log.error("Payment not valid. Status: {}", payment.getStatus());
            return false;
        }
        log.info("Payment valid");
        return true;
    }

    public void createPayment(Order order, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.NOT_PAID);

        savePayment(payment);
        log.info("Payment id {} created for order with id {}", payment.getId(), order.getId());
    }

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order id {} " + orderId));
    }

    public PaymentDetailsDTO getPaymentDetails(Long orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        PaymentDetailsDTO paymentDetailsDTO = PaymentDetailsDTO.fromPayment(payment);

        return paymentDetailsDTO;
    }
}