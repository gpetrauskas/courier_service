package com.example.courier.service.order.query;

import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.validation.shared.InternalUseOnly;

import java.util.List;

/**
 * Handles read operations for orders.
 *
 * Provides for both admin and user cases.
 * */
public interface OrderQueryService {
    /*
    * Admin Methods
    */

    /**
     * Fetch paginated {@link AdminOrderResponseDTO} list of detailed orders for administration.
     * Optionally filtered.
     *
     * @param page page index.
     * @param size page size.
     * @param orderStatus optional order status filter (can be nullable/blank).
     * @param id optional order id filter (can be nullable).
     * @return a page of {@link AdminOrderResponseDTO} an admin focused full order views
     * (including payment, parcel, address and user details).
     * @throws org.springframework.security.access.AccessDeniedException if user is not an admin.
     * */
    PaginatedResponseDTO<AdminOrderResponseDTO> getDetailedOrdersForAdmin(int page, int size, String orderStatus, Long id);

    /**
     * Fetch paginated {@link OrderDTO} list of orders filtered by task type for task assignment.
     *
     * @param page a page index.
     * @param size page size.
     * @param taskType task type of orders that will be used by specifications.
     * @return a page of order DTOs for assignment workflow.
     * @throws org.springframework.security.access.AccessDeniedException if user is not an admin.
     * */
    PaginatedResponseDTO<OrderDTO> getOrdersForTaskAssignment(int page, int size, String taskType);

    /**
     * Fetch single order in admin view.
     *
     * @param oderId the order id.
     * @return {@link AdminOrderResponseDTO} an detailed admin response.
     * @throws org.springframework.security.access.AccessDeniedException if the user is not ad admin.
     * @throws com.example.courier.exception.ResourceNotFoundException if not found
     * */
    AdminOrderResponseDTO getAdminOrderById(Long oderId);

    /**
     * Fetch all orders that reference any of provided parcels.
     *
     * @param parcels parcel details to match.
     * @return matching order.
     * @throws org.springframework.security.access.AccessDeniedException if user is not an admin.
     * */
    @InternalUseOnly
    List<Order> fetchAllByParcelDetails(List<Parcel> parcels);

    /**
     * Fetch single order entity by id.
     *
     * @param id an order id.
     * @return order entity.
     * @throws com.example.courier.exception.ResourceNotFoundException if not found.*/
    Order fetchById(Long id);

    /*
    * User Methods
    */

    /**
     * Fetch paginated list of orders owned by current authenticated user.
     *
     * @param page a page index.
     * @param size size of the page.
     * @return page of order DTOs.
     * */
    PaginatedResponseDTO<OrderDTO> findUserOrders(int page, int size);

    /**
     * Fetch order by given order id that belongs to current user and map it to DTO.
     *
     * @param id order id.
     * @return {@link OrderDTO} the order DTO.
     * */
    OrderDTO findSelfOrderById(Long id);

    /**
     * Fetch order entity by given id that belongs tu current authenticated user.
     *
     * @param orderId an id of the order.
     * @return {@link Order} a fetched order
     * */
    Order getOrderByIdAndCurrentPersonId(Long orderId);
}
