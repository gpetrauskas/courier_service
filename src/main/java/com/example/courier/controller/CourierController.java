package com.example.courier.controller;

import com.example.courier.dto.CourierTaskDTO;
import com.example.courier.service.CourierService;
import com.example.courier.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/courier")
public class CourierController {

    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);
    @Autowired
    private CourierService courierService;


    @PreAuthorize("hasRole('COURIER')")
    @GetMapping("/currentTaskList")
    public ResponseEntity<?> getCurrentTaskList() {

        logger.info("hello");
        Long courierId = AuthUtils.getAuthenticatedPersonId();
        List<CourierTaskDTO> taskList = courierService.getCurrentTaskList(courierId);

        return ResponseEntity.ok(taskList);
    }

    @PreAuthorize("hasRole('COURIER')")
    @PostMapping("/updateTaskItemNotes/{taskItemId}")
    public ResponseEntity<Map<String, String>> updateTaskItemNotes(
            @PathVariable Long taskItemId,
            @RequestBody Map<String, String> payLoad) {

        courierService.checkAndUpdateTaskItem(taskItemId, payLoad);

        Map<String, String> response = new HashMap<>();
        response.put("success", "Note successfully added.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
