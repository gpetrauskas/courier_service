package com.example.courier.service.order.command;

import com.example.courier.domain.Order;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;

import java.util.Map;

public interface OrderCommandService {
    /*
    * Admin methods
    */
    void updateOrderSection(OrderSectionUpdateRequest updateRequest);


    /*
    * User methods
    */
    Map<String, String> placeOrder(OrderDTO orderDTO);
    void cancelOrder(Long orderId);




    void save(Order order);
}
