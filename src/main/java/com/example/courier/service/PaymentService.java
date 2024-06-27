package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.PaymentDTO;
import com.example.courier.exception.*;
import com.example.courier.payment.handler.PaymentHandler;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private List<PaymentHandler> paymentHandlers;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public ResponseEntity<String> processPayment(PaymentDTO paymentDTO, Payment payment) {
        try {
            Order order = orderRepository.findById(payment.getOrder().getId()).orElseThrow(() ->
                    new OrderNotFoundException("Order not found"));
            User user = userRepository.findById(payment.getOrder().getUser().getId()).orElseThrow(() ->
                    new UserNotFoundException("User not found"));

            for (PaymentHandler handler : paymentHandlers) {
                if (handler.isSupported(paymentDTO)) {
                    ResponseEntity<String> response = handler.handle(paymentDTO, payment);
                    if (!response.getStatusCode().equals(HttpStatus.OK)) {
                        throw new PaymentFailedException(response.getBody());
                    }

                    payment.setStatus(PaymentStatus.PAID);
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.getPackageDetails().setStatus(PackageStatus.PICKING_UP);

                    orderRepository.save(order);
                    paymentRepository.save(payment);
                    return response;
                }
            }

            throw new PaymentFailedException("No handler for provided payment");

        } catch (PaymentFailedException e) {
            log.error("Error occurred during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during payment. " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during payment");
        }
    }
}
