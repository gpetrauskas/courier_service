package com.example.courier.controller;

import com.example.courier.dto.PackageDTO;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.CourierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courier")
public class CourierController {

    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourierService courierService;


    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/pickupPackages")
    public ResponseEntity<?> getAllOrdersToPickUp(Principal principal) {

        try {
            List<PackageDTO> list = courierService.getAllPackagesToPickUp();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status not found.");
        }
    }
}
