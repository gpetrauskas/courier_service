package com.example.courier.controller;

import com.example.courier.service.person.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

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
}

