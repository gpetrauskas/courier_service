package com.example.courier.controller;

import com.example.courier.dto.LoginDTO;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {
        Map<String, String> loginResponse = new HashMap<>();
        try {
            Map<String, String> loginResult = authService.loginUser(loginDTO, response);
            loginResponse.put("message", loginResult.get("message"));

            logger.info("User {} logged in successfully", loginDTO.email());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            loginResponse.put("error", e.getMessage());
            logger.error("Error occurred while user {} tried to login: {}", loginDTO.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }
}
