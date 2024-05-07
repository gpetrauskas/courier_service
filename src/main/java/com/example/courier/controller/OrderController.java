package com.example.courier.controller;

import com.example.courier.domain.Order;
import com.example.courier.domain.User;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.UserResponseDTO;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/getUserOrders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserOrders() {
        List<OrderDTO> orders;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName());

            orders = orderService.findUserOrders(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred finding orders.");
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/getAllOrders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<Order> order = orderRepository.findAll();

        List<OrderDTO> orderDTOs = order.stream()
                .map(o -> {
                    OrderDTO orderDTO = new OrderDTO(o.getId(), o.getSenderAddress(),
                            o.getRecipientAddress(), o.getPackageDetails(), o.getDeliveryPreferences(),
                            o.getStatus(), o.getCreateDate());
                    return orderDTO;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName());

            BigDecimal shippingCost = orderService.calculateShippingCost(orderDTO);

            orderService.placeOrder(user.getId(), orderDTO, shippingCost);
            return ResponseEntity.ok("Order placed successfully. Shipping cost: " + shippingCost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred placing order.");
        }
    }

    @GetMapping("/trackOrder/{trackingNumber}")
    public ResponseEntity<String> trackOrder(@PathVariable String trackingNumber) {
        try {
            logger.info("Tracking package info with tracking id: " + trackingNumber);
            String orderStatus = trackingService.getPackageStatus(trackingNumber);
            logger.info("Package status with tracking id: " + trackingNumber + " was found.");
            return ResponseEntity.ok(orderStatus);
        } catch (Exception e) {
            logger.error("Package with tracking number: " + trackingNumber + " was not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
