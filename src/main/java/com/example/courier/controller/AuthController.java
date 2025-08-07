package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.LoginDTO;
import com.example.courier.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {
        try {
            ApiResponseDTO loginResult = authService.loginUser(loginDTO, response);
            logger.info("User {} logged in successfully", loginDTO.email());
            return ResponseEntity.ok(loginResult);
        } catch (RuntimeException e) {
            logger.error("Error occurred while user {} tried to login: {}", loginDTO.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO("error", e.getMessage()));
        }
    }
}
