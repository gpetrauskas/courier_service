package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.service.task.TaskItemService;
import com.example.courier.validation.shared.NotNullOrEmpty;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taskItem")
public class TaskItemController {

    private static final Logger logger = LoggerFactory.getLogger(TaskItemController.class);
    private final TaskItemService taskItemService;

    public TaskItemController(TaskItemService taskItemService) {
        this.taskItemService = taskItemService;
    }

    @GetMapping("/getAllByTaskType")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllByTaskType(@RequestParam("taskType") String taskType) {

        return null;
    }

    @PutMapping("remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> remove(
            @RequestParam @NotNullOrEmpty(message = "Task id cannot be empty.") Long taskId,
            @RequestParam @NotNullOrEmpty(message = "Item id cannot be empty.") Long itemId) {

        taskItemService.removeItemFromTask(taskId, itemId);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Task Item was successfully removed from the list."));
    }

    @PutMapping("updateStatus")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public ResponseEntity<ApiResponseDTO> updateStatus(
            @NotNullOrEmpty @RequestParam Long itemId,
            @NotNullOrEmpty @RequestParam String status) {
        taskItemService.updateStatus(itemId, status);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Successfully updated Task Item status"));
    }

    @PutMapping("updateNote/{taskItemId}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<UpdateTaskItemNotesResponse> updateNotes(
            @PathVariable Long taskItemId,
            @Valid @RequestBody UpdateTaskItemNotesRequest notesRequest
    ) {
        return ResponseEntity.ok(taskItemService.updateNote(notesRequest, taskItemId));
    }


}
