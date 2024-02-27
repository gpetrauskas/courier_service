package com.example.courier.controller;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Package;
import com.example.courier.domain.User;
import com.example.courier.dto.UserDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PackageRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.AdminService;
import com.example.courier.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/updateProductStatus/{trackingNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProductStatus(@PathVariable String trackingNumber, @RequestParam String newStatus) {
        try {
            if (!PackageStatus.isValidStatus(newStatus)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status.");
            }

            Package packageDetails = packageRepository.findByTrackingNumber(trackingNumber).orElseThrow(() ->
                    new NoSuchElementException("Package not found."));

            packageDetails.setStatus(newStatus);
            packageRepository.save(packageDetails);

            return ResponseEntity.ok("Package status updated successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem occurred during package status update;");
        }
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());

        return ResponseEntity.ok(authentication.getAuthorities());
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> allUsers = adminService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<UserResponseDTO> userResponseDTO = adminService.findUserById(id);
        if (userResponseDTO.isPresent()) {
            return ResponseEntity.ok(userResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found.");
        }
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.ok("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Where was an error during registration: " + e.getMessage());
        }
    }
}
