package com.example.courier.controller;

import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.service.task.TaskItemService;
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

    @PutMapping("updateNote/{taskItemId}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<UpdateTaskItemNotesResponse> updateNotes(
            @PathVariable Long taskItemId,
            @Valid @RequestBody UpdateTaskItemNotesRequest notesRequest
    ) {
        return ResponseEntity.ok(taskItemService.updateNote(notesRequest, taskItemId));
    }
}
