package com.example.courier.controller;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import com.example.courier.domain.PricingOption;
import com.example.courier.domain.User;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.dto.UserDetailsDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PackageRepository;
import com.example.courier.service.AdminService;
import com.example.courier.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/updateProductStatus/{trackingNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProductStatus(@PathVariable String trackingNumber, @RequestParam PackageStatus newStatus) {
        try {
            if (!PackageStatus.isValidStatus(newStatus.name())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status.");
            }

            Package packageDetails = packageRepository.findByTrackingNumber(trackingNumber).orElseThrow(() ->
                    new NoSuchElementException("Package not found."));

            packageDetails.setStatus(newStatus);
            packageRepository.save(packageDetails);

            return ResponseEntity.ok("Package status updated successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem occurred during package status update;");
        }
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());

        return ResponseEntity.ok(authentication.getAuthorities());
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {

        logger.info("role {}, search {}", role, search);

        Page<UserResponseDTO> userPage = adminService.findAllUsers(page, size, role, search);

        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<UserDetailsDTO> userDetailsDTO = adminService.findUserById(id);
        if (userDetailsDTO.isPresent()) {
            return ResponseEntity.ok(userDetailsDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found.");
        }
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.ok("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Where was an error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/updateUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody UserDetailsDTO userDetailsDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Admin controller: before userUpdate");
            adminService.updateUser(id, userDetailsDTO);
            logger.info("User updated successfully.");
            response.put("message", "User was updated successfully");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            logger.info("User was not found.");
            response.put("error", "User was not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.info("Unexpected error occurred: {}", e.getMessage());
            response.put("error", "Error Occurred during user update");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/updateOrder/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderSection(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Trying to edit order with id: {} details", id);
            adminService.updateSection(updateData);
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while editing order details with order id: {}", id, e);
            response.put("message", "Error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Request to delete user with id: {}", id);
            adminService.deleteUser(id);
            response.put("success", "User deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            logger.warn("deleteUser: {}", e.getMessage());
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.warn("deleteUser: Error occurred: {}", e.getMessage());
            response.put("error", "Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status
    ) {
        Page<AdminOrderDTO> orderPage = adminService.getAllOrders(page, size, userId, status);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        AdminOrderDTO adminOrderDTO = adminService.getOrderById(id);
        if (adminOrderDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order was not found.");
        }
        return ResponseEntity.ok(adminOrderDTO);
    }

    @GetMapping("/report/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateUserReport() {
            String report = adminService.generateUserReport();
            return ResponseEntity.ok(report);
    }

    @GetMapping("/report/order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateOrderReport() {
        String report = adminService.generateOrderReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/pricing-options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPricingOptionById(@PathVariable Long id) {
        Optional<PricingOption> option = adminService.getPricingOptionById(id);
        if (option != null) {
            return ResponseEntity.ok(option);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pricing option was not found");
        }
    }

    @PostMapping("/create-pricing-option")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPricingOption(@RequestBody PricingOption pricingOption) {
        Map<String, String> response = new HashMap<>();
        try {
            adminService.createPricingOption(pricingOption);
            response.put("message", "New pricing option was added successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error occurred while adding new pricing option. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/updatePricingOption/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updatePricingOption(@PathVariable Long id,
                                                      @RequestBody PricingOption pricingOption) {
        try {
            adminService.updatePricingOption(id, pricingOption);
            return ResponseEntity.ok("Pricing option was updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePricingOption/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePricingOption(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            ResponseEntity<String> message = adminService.deletePricingOption(id);
            response.put("message", message.getBody());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error occured while deleting pricing option", e.getMessage());
            logger.info("Error occurred while deleting pricing option: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

