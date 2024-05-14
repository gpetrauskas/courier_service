package com.example.courier.service;

import com.example.courier.domain.User;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceMethod {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public void addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found."));

    }
}
