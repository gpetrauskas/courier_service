package com.example.courier.service;

import com.example.courier.dto.OrderDTO;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    public void placeOrder(OrderDTO orderDTO) {

    }

    public BigDecimal calculateShippingCost(OrderDTO orderDTO) {
        return null;
    }
}
