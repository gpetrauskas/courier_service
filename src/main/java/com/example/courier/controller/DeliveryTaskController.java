package com.example.courier.controller;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.dto.*;
import com.example.courier.service.DeliveryTaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/deliveryTaskManagement")
public class DeliveryTaskController {

    @Autowired
    private DeliveryTaskService deliveryTaskService;
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskController.class);


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> createNewList(@RequestBody @Valid CreateTaskDTO createTaskDTO) {
        logger.info("check {} and {}", createTaskDTO.courierId(), createTaskDTO.adminId());
        deliveryTaskService.createNewDeliveryTask(createTaskDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery task was successfully created"));
    }

    @GetMapping("/availableItemsCount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getAvailableItemsCount() {
        Map<String, Long> response = deliveryTaskService.getAvailableItemsCount();

        return ResponseEntity.ok(response);
    }

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

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> cancel(@PathVariable Long id) {

        deliveryTaskService.cancel(id);

        return ResponseEntity.ok(new ApiResponseDTO("success", "Task was successfully canceled. IDL " + id));
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
