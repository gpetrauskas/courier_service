package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.service.DeliveryTaskItemService;
import com.example.courier.validation.shared.NotEmptyField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveryTaskItem")
public class DeliveryTaskItemController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskItemController.class);

    @Autowired
    private DeliveryTaskItemService deliveryTaskItemService;

    @GetMapping("/getAllByTaskType")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllByTaskType(@RequestParam("taskType") String taskType) {

        return null;
    }

    @PutMapping("remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> remove(
            @RequestParam @NotEmptyField(message = "Task id cannot be empty.") Long taskId,
            @RequestParam @NotEmptyField(message = "Item id cannot be empty.") Long itemId) {

        deliveryTaskItemService.removeItemFromTask(taskId, itemId);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Task Item was successfully removed from the list."));
    }
}
