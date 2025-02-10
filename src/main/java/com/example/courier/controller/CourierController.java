package com.example.courier.controller;

import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.service.CourierService;
import com.example.courier.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
        List<DeliveryTaskDTO> taskList = courierService.getCurrentTaskList(courierId);

        return ResponseEntity.ok(taskList);
    }
}
