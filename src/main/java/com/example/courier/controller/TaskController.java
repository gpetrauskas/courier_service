package com.example.courier.controller;

import com.example.courier.dto.*;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.service.task.TaskCommandService;
import com.example.courier.service.task.TaskQueryService;
import com.example.courier.validation.shared.NotNullOrEmpty;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryTaskManagement")
public class TaskController {

    private final TaskCommandService commandService;
    private final TaskQueryService queryService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskCommandService commandService, TaskQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> createNewList(@RequestBody @Valid CreateTaskDTO createTaskDTO) {
        logger.info("check {} and {}", createTaskDTO.courierId(), createTaskDTO.adminId());
        commandService.initiateNewTask(createTaskDTO);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Delivery task was successfully created"));
    }

    @GetMapping("getAllDeliveryTaskLists")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<PaginatedResponseDTO<? extends TaskBase>> getAllDeliveryTaskLists(
            @ModelAttribute DeliveryTaskFilterDTO filterDTO
    ) {
        logger.info("fetching {}", filterDTO);
        PaginatedResponseDTO<? extends TaskBase> list = queryService.getAllTasksList(filterDTO);

        return ResponseEntity.ok(list);
    }

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> cancel(@PathVariable Long id) {
        commandService.cancel(id);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Task was successfully canceled. IDL " + id));
    }

    @PutMapping("changeTaskStatus/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> changeTaskStatus(
            @PathVariable Long taskId,
            @RequestBody String newStatus
    ) {
        commandService.changeStatus(taskId, newStatus);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Status for Task ID: " + taskId +
                " was successfully changed to: " + newStatus));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<List<CourierTaskDTO>> getCurrentTask() {
        List<CourierTaskDTO> taskDTO = queryService.getCurrentCourierTask();
        return ResponseEntity.ok(taskDTO);
    }

    @PutMapping("/checkIn/{taskId}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<ApiResponseDTO> checkIn(@PathVariable Long taskId) {
        logger.info("im here??");
        commandService.checkIn(taskId);
        return ResponseEntity.ok(new ApiResponseDTO("success", String.format("Courier successfully" +
                " check-in with task %s", taskId)));
    }

    @PostMapping("/{taskId}/removeItem/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> removeItem(
            @PathVariable @NotNullOrEmpty(message = "Task id cannot be empty.") Long taskId,
            @PathVariable @NotNullOrEmpty(message = "Task id cannot be empty.") Long itemId) {
        return ResponseEntity.ok(commandService.removeTaskItemFromTask(taskId, itemId));
    }

    @PostMapping("/{taskId}/updateItemStatus/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<ApiResponseDTO> updateStatus(
            @NotNullOrEmpty @PathVariable Long itemId,
            @NotNullOrEmpty @RequestParam String status,
            @NotNullOrEmpty @PathVariable Long taskId) {
        return ResponseEntity.ok(commandService.updateTaskItemStatus(itemId, status, taskId));
    }

    @PostMapping("/updateNote/{taskItemId}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<UpdateTaskItemNotesResponse> addTaskItemNote(
            Long taskItemId,
            UpdateTaskItemNotesRequest request) {
        return ResponseEntity.ok(commandService.addItemNote(request, taskItemId));
    }

}
