package com.example.courier.controller;

import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.service.DeliveryTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryTaskManagement")
public class DeliveryTaskController {

    @Autowired
    private DeliveryTaskService deliveryTaskService;

    @GetMapping("getAllDeliveryTaskLists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryTaskDTO>> getAllDeliveryTaskLists() {
        List<DeliveryTaskDTO> list = deliveryTaskService.getAllDeliveryLists();



        return ResponseEntity.ok(list);
    }
}
