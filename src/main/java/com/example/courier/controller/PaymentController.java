package com.example.courier.controller;

import com.example.courier.dto.PaymentDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> makePayment(@PathVariable Long orderId, @RequestBody PaymentDTO paymentDTO, Principal principal) {
        try {
            return paymentService.processPayment(paymentDTO, orderId, principal);
        } catch (RuntimeException e) {
            log.error("Unexpected error during payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error durin payent");
        }
    }

    @PostMapping("/processPayment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
          //  paymentService.processPayment(paymentDTO);
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem occurred during payment processing");
        }
    }

    @GetMapping("/getPaymentDetails/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long orderId, Principal principal) {
        try {
            PaymentDetailsDTO paymentDetailsDTO = paymentService.getPaymentDetails(orderId);
            return ResponseEntity.ok(paymentDetailsDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred finding payment.");
        }
    }
}
