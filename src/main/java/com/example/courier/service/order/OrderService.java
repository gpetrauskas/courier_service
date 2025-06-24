package com.example.courier.service.order;

import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.User;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Map<String, Object> placeOrder(OrderDTO orderDTO);
    Page<OrderDTO> findUserOrders(int page, int size);
    void cancelOrder(Long orderId);
    Order findOrderById(Long orderId);
    OrderDTO findUserOrderDTOById(Long orderId);
    void orderSectionUpdate(OrderSectionUpdateRequest updateRequest);
    Page<AdminOrderResponseDTO> getAllOrdersForAdmin(int page, int size, String orderStatus, Long id);
    List<Order> fetchAllByParcelDetails(List<Parcel> parcels);
    PaginatedResponseDTO<OrderDTO> fetchAllTaskOrdersByTaskType(int page, int size, String taskType);
    AdminOrderDTO getAdminOrderById(Long id);
    //void saveOrder(Order order);
   // void updateAndSaveOrderStatusConfirmed(Long orderId);
}