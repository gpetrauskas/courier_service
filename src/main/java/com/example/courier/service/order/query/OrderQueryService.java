package com.example.courier.service.order.query;

import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.validation.shared.InternalUseOnly;

import java.util.List;

public interface OrderQueryService {
    /*
    * Admin Methods
    */
    PaginatedResponseDTO<AdminOrderResponseDTO> getDetailedOrdersForAdmin(int page, int size, String orderStatus, Long id);
    PaginatedResponseDTO<OrderDTO> getOrdersForTaskAssignment(int page, int size, String taskType);
    AdminOrderResponseDTO getAdminOrderById(Long oderId);
    @InternalUseOnly
    List<Order> fetchAllByParcelDetails(List<Parcel> parcels);
    Order fetchById(Long id);

    /*
    * User Methods
    */
    PaginatedResponseDTO<OrderDTO> findUserOrders(int page, int size);
    OrderDTO findSelfOrderById(Long id);
}
