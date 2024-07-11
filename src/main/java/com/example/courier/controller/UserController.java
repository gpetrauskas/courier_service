package com.example.courier.controller;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Received a registration request for user: {}", userDTO.email());
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUser(userDTO);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while registering: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {
        try {
            String token = userService.loginUser(loginDTO);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return ResponseEntity.ok("Login successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> test() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userRepository.findByEmail(userEmail);
        String fullName = user.getName();
        return ResponseEntity.ok("Hello, " + fullName + "!");
    }

    @GetMapping("/ccList")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> testing(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        System.out.println(user.getEmail());
        List<PaymentMethod> paymentMethods = user.getPaymentMethods();
        if (user.getPaymentMethods() == null || user.getPaymentMethods().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Payment Methods found for the user");
        }

        return ResponseEntity.ok(paymentMethods);
    }
}
