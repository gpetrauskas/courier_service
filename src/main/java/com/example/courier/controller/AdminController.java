package com.example.courier.controller;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Package;
import com.example.courier.domain.PricingOption;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PackageRepository;
import com.example.courier.service.AdminService;
import com.example.courier.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public ResponseEntity<?> updateProductStatus(@PathVariable String trackingNumber, @RequestParam String newStatus) {
        try {
            if (!PackageStatus.isValidStatus(newStatus)) {
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
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> allUsers = adminService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<UserResponseDTO> userResponseDTO = adminService.findUserById(id);
        if (userResponseDTO.isPresent()) {
            return ResponseEntity.ok(userResponseDTO);
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
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            logger.info("Admin controller: before userUpdate");
            adminService.updateUser(id, userDTO);
            logger.info("User updated successfully.");
            return ResponseEntity.ok("User was updated successfully.");
        } catch (UserNotFoundException e) {
            logger.info("User was not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found.");
        } catch (Exception e) {
            logger.info("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred during user update.");
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            logger.info("Request to delete user with id: {}", id);
            adminService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (UserNotFoundException e) {
            logger.warn("deleteUser: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found");
        } catch (Exception e) {
            logger.warn("deleteUser: Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting user.");
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminOrderDTO> getAllOrders() {
        return adminService.getAllOrders();
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
    public ResponseEntity<String> createPricingOption(@RequestBody PricingOption pricingOption) {
        try {
            adminService.createPricingOption(pricingOption);
            return ResponseEntity.ok("New pricing option was added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
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

}

