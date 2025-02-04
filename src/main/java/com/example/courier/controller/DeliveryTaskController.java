package com.example.courier.controller;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.service.DeliveryTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryTaskManagement")
public class DeliveryTaskController {

    @Autowired
    private DeliveryTaskService deliveryTaskService;
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskController.class);

    @GetMapping("getAllDeliveryTaskLists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<DeliveryTaskDTO>> getAllDeliveryTaskLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courierId,
            @RequestParam(required = false) TaskType taskType,
            @RequestParam(required = false) DeliveryStatus status
    ) {

        logger.info("Fetching delivery tasks list for page {}, size {}, " +
                "courier {}, task type {}, delivery status {}", page, size, courierId, taskType, status);

        PaginatedResponseDTO<DeliveryTaskDTO> list = deliveryTaskService.getAllDeliveryLists(page, size, courierId, taskType, status);

        return ResponseEntity.ok(list);
    }
}
