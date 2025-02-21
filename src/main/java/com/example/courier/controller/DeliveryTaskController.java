package com.example.courier.controller;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.DeliveryTaskFilterDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.service.DeliveryTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @ModelAttribute DeliveryTaskFilterDTO filterDTO
    ) {
        logger.info("fetching {}", filterDTO);
        PaginatedResponseDTO<DeliveryTaskDTO> list = deliveryTaskService.getAllDeliveryLists(filterDTO.page(),
                filterDTO.size(), filterDTO.courierId(), filterDTO.taskListId(),
                filterDTO.taskType(), filterDTO.deliveryStatus());

        return ResponseEntity.ok(list);
    }

    @PostMapping("changeTaskStatus/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeTaskStatus(
            @PathVariable Long taskId,
            @RequestBody DeliveryStatus newStatus
    ) {

        deliveryTaskService.changeTaskStatus(taskId, newStatus);

        return ResponseEntity.ok("success");
    }
}
