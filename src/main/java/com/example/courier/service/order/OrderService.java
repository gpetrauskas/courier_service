package com.example.courier.service.order;

import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    Long placeOrder(Long id, OrderDTO orderDTO);
    List<OrderDTO> findUserOrders(User user);
    void cancelOrder(Long orderId, Principal principal);
    Order findOrderById(Long orderId);
    OrderDTO findUserOrderDTOById(Long orderId, Principal principal);
    List<OrderDTO> findAllOrders();
    void orderSectionUpdate(OrderSectionUpdateRequest updateRequest);
    Page<AdminOrderResponseDTO> getAllOrdersForAdmin(int page, int size, String orderStatus, String parcelStatus, Long id);
    List<Order> fetchAllByParcelDetails(List<Parcel> parcels);
    //void saveOrder(Order order);
   // void updateAndSaveOrderStatusConfirmed(Long orderId);
}