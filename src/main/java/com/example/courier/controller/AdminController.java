package com.example.courier.controller;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.*;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.repository.PackageRepository;
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
    private PackageRepository packageRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private DeliveryTaskItemRepository deliveryTaskItemRepository;

    @Autowired
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/updateProductStatus/{trackingNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateProductStatus(@PathVariable String trackingNumber, @RequestParam PackageStatus newStatus) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!PackageStatus.isValidStatus(newStatus.name())) {
                response.put("error", "Invalid status.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Package packageDetails = packageRepository.findByTrackingNumber(trackingNumber).orElseThrow(() ->
                    new NoSuchElementException("Package not found."));

            packageDetails.setStatus(newStatus);
            packageRepository.save(packageDetails);

            response.put("success", "Package status updated successfully");
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("error", "Problem occurred during package status change: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
    public ResponseEntity<PaginatedResponseDTO<PersonResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {

        logger.info("role {}, search {}", role, search);

        PaginatedResponseDTO<PersonResponseDTO> response = adminService
                .findAllUsersPaginated(page, size, role, search);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<PersonDetailsDTO> personDetailsDTO = adminService.findPersonById(id);
        if (personDetailsDTO.isPresent()) {
            return ResponseEntity.ok(personDetailsDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found.");
        }
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody RegistrationDTO registrationDTO) {
        try {
            //userService.registerUser(registrationDTO);
            return ResponseEntity.ok("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Where was an error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/updateUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody PersonDetailsDTO personDetailsDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Admin controller: before userUpdate");
            adminService.updateUser(id, personDetailsDTO);
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
            logger.warn("Error occurred: {}", e.getMessage());
            response.put("error", "Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/banUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Request to ban user with id {}", id);
            adminService.banUser(id);

            response.put("success", "User with id " + id + ", was successfully banned.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            logger.warn("User with id {}, was not found", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/unbanUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Request to unban user with id {}", id);
            adminService.unbanUser(id);

            response.put("success", "User with id" +  id + " was unbanned successfully.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            logger.warn("User with id {}, was not found", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @PostMapping("/createCourierDeliveryList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCourierDeliveryList(@RequestBody CreateTaskDTO taskDTO) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminService.getAuthenticatedAdmin();

        CreateTaskDTO updatedTaskDTO = new CreateTaskDTO(
                taskDTO.courierId(),
                admin.getId(),
                taskDTO.parcelsIds(),
                taskDTO.taskType()
        );

        try {
            logger.info("Parcels IDs: " + updatedTaskDTO.parcelsIds());
            logger.info("Courier ID: " + updatedTaskDTO.courierId());
            logger.info("Task Type: " + updatedTaskDTO.taskType());

            adminService.createNewCourierTask(updatedTaskDTO);

            response.put("success", "Courier Task List was created successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error occurred while creating courier list. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/getItemsForTheListCount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Number>> getItemsForTheListCount() {
        Map<String, Number> response = adminService.getItemsForTheListCount();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getItemsByStatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getItemsByStatus(@RequestParam("status") String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> packages = adminService.getItemsByStatus(page, size, status);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/getAvailableCouriers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourierDTO>> getAvailableCouriers() {
        List<CourierDTO> availableCouriers = adminService.getAvailableCouriers();

        return ResponseEntity.ok(availableCouriers);
    }

    @GetMapping("/testGet")
    public ResponseEntity<?> getTest() {

        List<DeliveryTaskItem> list = deliveryTaskItemRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("getAllDeliveryLists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryTaskDTO>> getAllDeliveryLists() {
        List<DeliveryTaskDTO> tasksList = adminService.getAllDeliveryLists();

        for (DeliveryTaskDTO taskDTO : tasksList) {
            for (DeliveryTaskItemDTO itemDTO : taskDTO.itemsList()) {
                System.out.println(itemDTO.packageDTO().status());
            }
        }

        return ResponseEntity.ok(tasksList);
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

