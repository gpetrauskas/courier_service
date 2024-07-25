package com.example.courier.controller;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.repository.PaymentMethodRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paymentMethods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        paymentMethodService.addPaymentMethod(user.getId(), paymentMethodDTO);

        return ResponseEntity.ok("Payment method added successfully.");
    }

    @GetMapping("/getSavedPaymentMethods")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSavedPaymentMethods() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

    @DeleteMapping("/delete/{paymentMethodId}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable Long paymentMethodId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not authenticated.");
        }

        User user = userRepository.findByEmail(authentication.getName());
        ResponseEntity<String> response = paymentMethodService.deletePaymentMethodById(user, paymentMethodId);

        return response;
    }
}
