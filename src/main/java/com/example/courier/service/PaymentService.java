package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.domain.User;
import com.example.courier.dto.PaymentDTO;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public void processPayment(PaymentDTO paymentDTO) {
        User user = userRepository.findById(paymentDTO.order().getUser().getId()).orElseThrow(() ->
                new RuntimeException("User not found."));
        Order order = orderRepository.findById(paymentDTO.order().getId()).orElseThrow(() ->
                new RuntimeException("Order not found."));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentDTO.paymentMethod());
        payment.setAmount(paymentDTO.amount());
        payment.setStatus("WAITING");

        try {
            if (paymentDTO.paymentMethod().equals("credit_card")) {
                /// logic
                payment.setStatus("PAID");
                order.setStatus("CONFIRMED");
                order.getPackageDetails().setStatus("PICKING_UP");
            } else if (paymentDTO.paymentMethod().equals("online_banking")) {
                /// logic
                payment.setStatus("PAID");
                order.setStatus("CONFIRMED");
                order.getPackageDetails().setStatus("PICKING_UP");
            }
        } catch (Exception e) {
            // fail
            payment.setStatus("FAILED");
            order.setStatus("PAYMENT_FAILED");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
