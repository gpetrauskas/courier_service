package com.example.courier.service.order;

import com.example.courier.domain.Order;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.OrderSectionUpdateRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Long placeOrder(Long id, OrderDTO orderDTO);
    List<OrderDTO> findUserOrders(User user);
    void cancelOrder(Long orderId, Principal principal);
    Order findOrderById(Long orderId);
    OrderDTO findUserOrderDTOById(Long orderId, Principal principal);
    List<OrderDTO> findAllOrders();
    void orderSectionUpdate(OrderSectionUpdateRequest updateRequest);
    //void saveOrder(Order order);
   // void updateAndSaveOrderStatusConfirmed(Long orderId);
}