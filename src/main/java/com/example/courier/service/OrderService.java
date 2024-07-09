package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    Long placeOrder(Long id, OrderDTO orderDTO);
    List<OrderDTO> findUserOrders(User user);
    void cancelOrder(Long orderId, Principal principal);
    Order findOrderById(Long orderId);
    List<OrderDTO> findAllOrders();
    //void saveOrder(Order order);
   // void updateAndSaveOrderStatusConfirmed(Long orderId);
}