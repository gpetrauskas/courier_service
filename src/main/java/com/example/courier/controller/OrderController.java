package com.example.courier.controller;

import com.example.courier.common.ParcelStatus;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.order.BaseOrderUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.service.order.OrderFacadeService;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.TrackingService;
import com.example.courier.service.auth.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private TrackingService trackingService;
    @Autowired
    private AuthService authService;
    @Autowired
    private DeliveryMethodService deliveryMethodService;
    @Autowired
    private OrderFacadeService orderFacadeService;

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody BaseOrderUpdateRequest updateRequest) {
        logger.info("Received update request: {}", updateRequest);
        orderFacadeService.updateSection(updateRequest);
        return ResponseEntity.ok("Order and/or its related information was updated");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<AdminOrderResponseDTO>> getAllOrdersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long id
    ) {
        Page<AdminOrderResponseDTO> orderDTOPage = orderService.getAllOrdersForAdmin(page, size, status, id);

        return ResponseEntity.ok(new PaginatedResponseDTO<>(
                orderDTOPage.getContent(),
                orderDTOPage.getNumber(),
                orderDTOPage.getTotalElements(),
                orderDTOPage.getTotalPages()
        ));
    }

    @GetMapping("/taskOrdersByTaskType")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<OrderDTO>> getAllOrdersByTaskType(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size,
            @RequestParam String taskType
    ) {
        PaginatedResponseDTO<OrderDTO> responseDTO = orderService.fetchAllTaskOrdersByTaskType(page, size, taskType);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDTO> getOrderById(@PathVariable Long id) {
       AdminOrderDTO adminOrderDTO  = orderService.getAdminOrderById(id);
       return ResponseEntity.ok(adminOrderDTO);
    }

    @GetMapping(value = "/getUserOrders", params = { "page", "size" })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserOrders(@RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<OrderDTO> orders = orderService.findUserOrders(page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders.getContent());
            response.put("totalOrders", orders.getTotalElements());
            response.put("totalPages", orders.getTotalPages());
            response.put("currentPage", orders.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred finding orders.");
        }
    }

    @GetMapping("/getUserOrderById/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId, Principal principal) {
        OrderDTO orderDTO = orderService.findUserOrderDTOById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO) {
        Map<String, Object> orderInfo = orderService.placeOrder(orderDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order was placed successfully. Order cost: " + orderInfo.get("amountToPay"));
        response.put("cost", orderInfo.get("amountToPay"));
        response.put("orderId", orderInfo.get("orderId"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancelOrder/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }

    @GetMapping("/trackOrder/{trackingNumber}")
    public ResponseEntity<?> trackOrder(@PathVariable String trackingNumber) {
        try {
            logger.info("Tracking parcel info with tracking id: " + trackingNumber);
            ParcelStatus orderStatus = trackingService.getParcelStatus(trackingNumber);
            logger.info("Parcel status with tracking id: " + trackingNumber + " was found.");
            return ResponseEntity.ok(orderStatus);
        } catch (Exception e) {
            logger.error("Parcel with tracking number: " + trackingNumber + " was not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}