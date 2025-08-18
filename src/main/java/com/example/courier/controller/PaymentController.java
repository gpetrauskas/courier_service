package com.example.courier.controller;

import com.example.courier.dto.request.PaymentRequestDTO;
import com.example.courier.dto.PaymentDetailsDTO;
import com.example.courier.dto.response.payment.PaymentResultResponse;
import com.example.courier.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResultResponse> makePayment(@PathVariable Long orderId, @RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResultResponse response = paymentService.processPayment(paymentRequestDTO, orderId);
        return ResponseEntity.status(
                    response.status().equals("success")
                        ? HttpStatus.OK
                        : HttpStatus.BAD_REQUEST)
                .body(response);
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
