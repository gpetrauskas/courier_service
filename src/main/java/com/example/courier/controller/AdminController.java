package com.example.courier.controller;

import com.example.courier.domain.*;
import com.example.courier.dto.*;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.service.AdminService;
import com.example.courier.service.AuthService;
import com.example.courier.service.RegistrationService;
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
    private AdminService adminService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private DeliveryTaskItemRepository deliveryTaskItemRepository;

    @Autowired
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());

        return ResponseEntity.ok(authentication.getAuthorities());
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDetailsDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findPersonById(id));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminOrderDTO> getOrderById(@PathVariable Long id) {
        AdminOrderDTO adminOrderDTO = adminService.getOrderById(id);
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

    @GetMapping("/delivery-options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryOptionBaseDTO> getDeliveryOptionById(@PathVariable Long id) {
        DeliveryOptionBaseDTO option = adminService.getDeliveryOptionById(id);
        return ResponseEntity.ok(option);
    }


    @GetMapping("/getItemsByStatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<OrderDTO>> getItemsByStatus(@RequestParam("status") String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        
        Page<OrderDTO> orderPage = adminService.getItemsByStatus(page, size, status);
        PaginatedResponseDTO<OrderDTO> response = new PaginatedResponseDTO<>(
                orderPage.getContent(),
                orderPage.getNumber(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAvailableCouriers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourierDTO>> getAvailableCouriers() {
        return ResponseEntity.ok(adminService.getAvailableCouriers());
    }

    @GetMapping("/testGet")
    public ResponseEntity<?> getTest() {

        List<DeliveryTaskItem> list = deliveryTaskItemRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/deleteDeliveryTaskItemFromTheTask/{taskId}/item/{itemId}/remove")
    public ResponseEntity<?> deleteDeliveryTaskItemFromTheTask(@PathVariable Long taskId, @PathVariable Long itemId) {
        Map<String, String> response = new HashMap<>();
        try {
            adminService.deleteDeliveryTaskItem(taskId, itemId);
            logger.info("Successfully deleted delivery task item. Task ID: {}, Item ID: {}", taskId, itemId);
            response.put("message", "Delivery task item removed successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error while deleting delivery task item", e);
            response.put("error", "Failed to delete delivery task item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/testinu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testinu() {
        logger.info("testinu");
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/cancelTask/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelTask(@PathVariable Long taskId) {
        Map<String, String> response = new HashMap<>();
        try {
            adminService.cancelTask(taskId);

            response.put("message", "Task with ID: " + taskId + " successfully canceled,");
            logger.info("Task with ID {} successfully canceled", taskId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error while canceling task with id {}: {}", taskId, e.getMessage(), e);
            response.put("error", "An error occrred while canceling the task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("registerCourier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerCourier(@RequestBody RegistrationDTO registrationDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            registrationService.createCourier(registrationDTO);
            response.put("success", "Courier registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Error while registering courier");
            response.put("error", "Error while registering courier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

