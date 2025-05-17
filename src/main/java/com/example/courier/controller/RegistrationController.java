package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.service.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/registration")
public class RegistrationController {
    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO> registerUser(@Valid @RequestBody RegistrationDTO registrationDTO) {
        return ResponseEntity.ok(registrationService.registerUser(registrationDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registerCourier")
    public ResponseEntity<ApiResponseDTO> registerCourier(@Valid @RequestBody RegistrationDTO registrationDTO) {
        return ResponseEntity.ok(registrationService.registerCourier(registrationDTO));
    }
}
