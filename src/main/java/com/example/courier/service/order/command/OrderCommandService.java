package com.example.courier.service.order.command;

import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;

import java.util.Map;

/**
 * Service responsible for handling "write operations" on orders
 *
 * <p>This includes both admin and user actions</p>
 * */
public interface OrderCommandService {
    /**
     * For admin use.
     * Updates a order section.
     *
     * @param updateRequest the request containing section type and updated vales
     * @throws org.springframework.security.access.AccessDeniedException if caller is not admin
     * @throws com.example.courier.exception.ResourceNotFoundException if order does not exists
     * @throws jakarta.validation.ValidationException if the update violates rules
     * */
    void updateOrderSection(OrderSectionUpdateRequest updateRequest);


    /*
    * User methods
    */

    /**
     * Place new order by the current authenticated user.
     *
     * <p>
     *     The order is initiated with provided details, linked to user and
     *     payment is created for the order. Persistence is handled internally
     *
     * @param orderDTO DTO containing order details
     * @return a response map with simple readable message, total cost and created order id.
     * @throws com.example.courier.exception.PaymentCreationException if payment creation fails
     * */
    Map<String, String> placeOrder(OrderDTO orderDTO);

    /**
     * Cancel an existing order owned by current authenticated user.
     *
     * @param orderId the ID of the order to cancel
     * @throws com.example.courier.exception.ResourceNotFoundException when order is not found
     * @throws com.example.courier.exception.OrderCancellationException if the order cannot be canceled
     * */
    void cancelOrder(Long orderId);
}
