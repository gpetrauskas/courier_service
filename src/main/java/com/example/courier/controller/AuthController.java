package com.example.courier.controller;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkAuthStatus(Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        return ResponseEntity.ok(isAuthenticated);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/checkAuth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return ResponseEntity.ok().build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
