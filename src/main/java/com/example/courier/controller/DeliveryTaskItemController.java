package com.example.courier.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveryTaskItem")
public class DeliveryTaskItemController {

    @GetMapping("/getByStatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllItemsByStatus(@RequestParam("status") String status) {
        return null;
    }
}
