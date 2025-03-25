package com.example.courier.controller;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.dto.*;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.task.AdminTaskDTO;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.service.task.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveryTaskManagement")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> createNewList(@RequestBody @Valid CreateTaskDTO createTaskDTO) {
        logger.info("check {} and {}", createTaskDTO.courierId(), createTaskDTO.adminId());
        taskService.createNewDeliveryTask(createTaskDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery task was successfully created"));
    }

    @GetMapping("/availableItemsCount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getAvailableItemsCount() {
        Map<String, Long> response = taskService.getAvailableItemsCount();

        return ResponseEntity.ok(response);
    }

    @GetMapping("getAllDeliveryTaskLists")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<PaginatedResponseDTO<? extends TaskBase>> getAllDeliveryTaskLists(
            @ModelAttribute DeliveryTaskFilterDTO filterDTO
    ) {
        logger.info("fetching {}", filterDTO);
        PaginatedResponseDTO<? extends TaskBase> list = taskService.getAllTaskLists(filterDTO);

        return ResponseEntity.ok(list);
    }

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> cancel(@PathVariable Long id) {
        taskService.cancel(id);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Task was successfully canceled. IDL " + id));
    }

    @PostMapping("changeTaskStatus/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> changeTaskStatus(
            @PathVariable Long taskId,
            @RequestBody DeliveryStatus newStatus
    ) {
        taskService.changeTaskStatus(taskId, newStatus);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Status for Task ID: " + taskId +
                " was successfully changed to: " + newStatus));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<List<CourierTaskDTO>> getCurrentTask() {
        List<CourierTaskDTO> taskDTO = taskService.getCourierCurrentTask();
        return ResponseEntity.ok(taskDTO);
    }

    @PutMapping("/checkIn/{taskId}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<ApiResponseDTO> checkIn(@PathVariable Long taskId) {
        taskService.checkIn(taskId);
        return ResponseEntity.ok(new ApiResponseDTO("success", String.format("Courier successfully" +
                " check-in with task %s", taskId)));
    }
}
