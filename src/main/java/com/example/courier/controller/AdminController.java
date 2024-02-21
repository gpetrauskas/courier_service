package com.example.courier.controller;

import com.example.courier.domain.Package;
import com.example.courier.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PackageRepository packageRepository;

    @PostMapping("/updateOrderStatus/{trackingNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String trackingNumber, String newStatus) {
        try {
            Package packageDetails = packageRepository.findByTrackingNumber(trackingNumber).orElseThrow(() ->
                    new RuntimeException("Package not found."));
            packageDetails.setStatus(newStatus);
            packageRepository.save(packageDetails);

            return ResponseEntity.ok("Package status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred during package status update;");
        }
    }
}
