package com.example.courier.service.order;

import com.example.courier.dto.*;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.service.order.command.OrderCommandService;
import com.example.courier.service.order.query.OrderQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    public OrderService(OrderCommandService commandService, OrderQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /*
    * Command service
    */
    public void orderSectionUpdate(OrderSectionUpdateRequest request) {
        commandService.updateOrderSection(request);
    }
    public Map<String, String> placeOrder(OrderDTO orderDTO) {
        return commandService.placeOrder(orderDTO);
    }
    public void cancelOrder(Long id) {
        commandService.cancelOrder(id);
    }

    /*
    * Query service
    */
    public PaginatedResponseDTO<AdminOrderResponseDTO> getDetailedOrdersForAdmin(int page, int size, String orderStatus, Long id) {
        return queryService.getDetailedOrdersForAdmin(page, size, orderStatus, id);
    }
    public PaginatedResponseDTO<OrderDTO> getOrdersForTaskAssignment(int page, int size, String taskType) {
        return queryService.getOrdersForTaskAssignment(page, size, taskType);
    }
    public AdminOrderResponseDTO getAdminOrderById(Long orderId) {
        return queryService.getAdminOrderById(orderId);
    }

    public PaginatedResponseDTO<OrderDTO> findUserOrders(int page, int size) {
        return queryService.findUserOrders(page, size);
    }

    public OrderDTO findSelfOrderById(Long orderId) {
        return queryService.findSelfOrderById(orderId);
    }
}