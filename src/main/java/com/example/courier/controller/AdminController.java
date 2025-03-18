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

    @GetMapping("/testGet")
    public ResponseEntity<?> getTest() {

        List<DeliveryTaskItem> list = deliveryTaskItemRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/testinu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testinu() {
        logger.info("testinu");
        return ResponseEntity.ok().body("ok");
    }
}

