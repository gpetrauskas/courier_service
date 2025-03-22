package com.example.courier.controller;

import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.request.UpdateTaskItemStatusRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.dto.response.UpdateTaskItemStatusResponse;
import com.example.courier.service.person.CourierService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/courier")
public class CourierController {

    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);
    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @PreAuthorize("hasRole('COURIER')")
    @PostMapping("/updateTaskItemNotes/{taskItemId}")
    public ResponseEntity<UpdateTaskItemNotesResponse> updateTaskItemNotes(
            @PathVariable Long taskItemId,
            @Valid @RequestBody UpdateTaskItemNotesRequest request) {

        courierService.updateTaskItemNotes(taskItemId, request);

        return ResponseEntity.ok(
                new UpdateTaskItemNotesResponse("Successfully added note for item id: ", taskItemId)
        );
    }

    @PreAuthorize("hasRole('COURIER')")
    @PostMapping("/updateTaskItemStatus/{taskItemId}")
    public ResponseEntity<UpdateTaskItemStatusResponse> updateTaskItemStatus(
            @PathVariable Long taskItemId,
            @Valid @RequestBody UpdateTaskItemStatusRequest payLoad) {

        courierService.updateTaskItemStatus(taskItemId, payLoad);

        return ResponseEntity.ok(
                new UpdateTaskItemStatusResponse("Status successfully changed")
        );
    }

    @PreAuthorize("hasRole('COURIER')")
    @PostMapping("/checkIn/{taskId}")
    public ResponseEntity<String> checkIn(@PathVariable Long taskId) {
        courierService.processCourierCheckIn(taskId);

        return ResponseEntity.ok("Courier checked in successfully.");
    }

}
