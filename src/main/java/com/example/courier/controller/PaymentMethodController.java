package com.example.courier.controller;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.payment.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/paymentMethods")
public class PaymentMethodController {

    private static final Logger log = LoggerFactory.getLogger(PaymentMethodController.class);
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        paymentMethodService.addPaymentMethod(paymentMethodDTO);

        return ResponseEntity.ok("Payment method added successfully.");
    }

    @GetMapping("/getSavedPaymentMethods")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSavedPaymentMethods() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("test test auth: " + authentication.getName() + " " + authentication.getPrincipal());
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not authenticated.");
        }

        User user = userRepository.findByEmail(authentication.getName());
        List<PaymentMethodDTO> savedMethods = paymentMethodService.getSavedPaymentMethods(user.getId());

        return ResponseEntity.ok(savedMethods);
    }

    @GetMapping("/getSavedPaymentMethods/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSavedPaymentMethods(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not authenticated.");
        }

        User user = userRepository.findByEmail(authentication.getName());
        PaymentMethod savedMethods = paymentMethodRepository.findById(id).orElseThrow(() ->
                 new RuntimeException("Error"));

        Optional<PaymentMethodDTO> paymentMethodDTO = paymentMethodService.getSavedPaymentMethod(id);
        return ResponseEntity.ok(paymentMethodDTO);
    }

    @GetMapping("/deactivate/{paymentMethodId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deactivatePaymentMethod(@PathVariable Long paymentMethodId) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            log.warn("User not authenticated");
            response.put("error", "User is not authenticated");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        log.info("Trying to delete a payment method");
        User user = userRepository.findByEmail(authentication.getName());

        try {
            paymentMethodService.deactivatePaymentMethodById(user, paymentMethodId);
            response.put("success", "Payment method was successfully deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Error during payemtn method deletion: {}", e.getMessage());
            response.put("error", "Error during payment method deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
