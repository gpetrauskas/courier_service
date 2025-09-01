package com.example.courier.controller;

import com.example.courier.common.ParcelStatus;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.order.BaseOrderUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.service.order.facade.OrderFacadeService;
import com.example.courier.service.TrackingService;
import com.example.courier.service.order.command.OrderCommandService;
import com.example.courier.service.order.query.OrderQueryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderCommandService commandService;
    private final OrderQueryService queryService;
    private final TrackingService trackingService;
    private final OrderFacadeService orderFacadeService;

    public OrderController(OrderCommandService commandService, OrderQueryService queryService,
                           TrackingService trackingService, OrderFacadeService orderFacadeService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.trackingService = trackingService;
        this.orderFacadeService = orderFacadeService;
    }

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
        return ResponseEntity.ok(queryService.getDetailedOrdersForAdmin(page, size, status, id));
    }

    @GetMapping("/getOrdersForTaskAssignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<OrderDTO>> getAllOrdersByTaskType(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size,
            @RequestParam String taskType
    ) {
        return ResponseEntity.ok(queryService.getOrdersForTaskAssignment(page, size, taskType));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderResponseDTO> getOrderById(@PathVariable Long id) {
       return ResponseEntity.ok(queryService.getAdminOrderById(id));
    }

    @GetMapping(value = "/getUserOrders", params = { "page", "size" })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaginatedResponseDTO<OrderDTO>> getUserOrders(
            @RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(queryService.findUserOrders(page, size));
    }

    @GetMapping("/getUserOrderById/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> getSelfOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(queryService.findSelfOrderById(orderId));
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addOrder(@RequestBody @Valid OrderDTO orderDTO) {
        return ResponseEntity.ok(commandService.placeOrder(orderDTO));
    }

    @PostMapping("/cancelOrder/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        commandService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }

    @GetMapping("/trackOrder/{trackingNumber}")
    public ResponseEntity<ParcelStatus> trackOrder(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(trackingService.getParcelStatus(trackingNumber));
    }

}