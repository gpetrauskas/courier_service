package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void placeOrder(Long id, OrderDTO orderDTO);
    List<OrderDTO> findUserOrders(User user);
    void cancelOrder(Order order);
    BigDecimal calculateShippingCost(OrderDTO orderDTO);
}
