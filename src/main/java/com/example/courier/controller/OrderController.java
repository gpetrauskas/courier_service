package com.example.courier.controller;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.OrderService;
import com.example.courier.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TrackingService trackingService;

    @GetMapping(value = "/getUserOrders", params = { "page", "size" })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserOrders(@RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName());
            List<OrderDTO> orders = orderService.findUserOrders(user);

            int start = (page - 1) * size;
            int end = Math.min(start + size, orders.size());
            List<OrderDTO> paginatedOrders = orders.subList(start, end);

            return ResponseEntity.ok(paginatedOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred finding orders.");
        }
    }

    @GetMapping("/getAllOrders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        try {
            List<Order> order = orderRepository.findAll();

            List<OrderDTO> orderDTOs = order.stream()
                    .map(OrderMapper.INSTANCE::toOrderDTO)
                    .toList();
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName());

            BigDecimal shippingCost = orderService.calculateShippingCost(orderDTO);

            orderService.placeOrder(user.getId(), orderDTO);
            return ResponseEntity.ok("Order placed successfully. Shipping cost: " + shippingCost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred placing order.");
        }
    }

    @PostMapping("/cancelOrder/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new OrderNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are ot authorized to cancel this order");
        }

        if (order.getStatus().equals(OrderStatus.CONFIRMED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order already confirmed and paid for. " +
                    "Contact support for more information how to abort it.");
        }

        orderService.cancelOrder(order);
        return ResponseEntity.ok("Order cancelled successfully.");
    }

    @GetMapping("/trackOrder/{trackingNumber}")
    public ResponseEntity<?> trackOrder(@PathVariable String trackingNumber) {
        try {
            logger.info("Tracking package info with tracking id: " + trackingNumber);
            PackageStatus orderStatus = trackingService.getPackageStatus(trackingNumber);
            logger.info("Package status with tracking id: " + trackingNumber + " was found.");
            return ResponseEntity.ok(orderStatus);
        } catch (Exception e) {
            logger.error("Package with tracking number: " + trackingNumber + " was not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
