package com.example.courier.controller;

import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    void placeOrder(Long id, OrderDTO orderDTO);
    List<OrderDTO> findUserOrders(User user);
    void cancelOrder();
}
