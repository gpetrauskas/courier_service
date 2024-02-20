package com.example.courier.controller;

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
        try {
            logger.info("Received a registration request for user: {}", userDTO.email());
            userService.registerUser(userDTO);
            return ResponseEntity.ok("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred during registration");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        String token = userService.login(loginDTO);
        Cookie cookie = new Cookie("jwt", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok("Login successful.");
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

}
