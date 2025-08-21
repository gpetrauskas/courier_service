package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaymentMethodDTO;
import com.example.courier.payment.method.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentMethods")
public class PaymentMethodController {

    private static final Logger log = LoggerFactory.getLogger(PaymentMethodController.class);
    private final PaymentMethodService service;

    public PaymentMethodController(PaymentMethodService service) {
        this.service = service;
    }

    @GetMapping("/getSavedPaymentMethods")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentMethodDTO>> getSavedPaymentMethods() {
        return ResponseEntity.ok(service.getSavedPaymentMethods());
    }

    @GetMapping("/getSavedPaymentMethods/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentMethodDTO> getSavedPaymentMethods(@PathVariable Long id) {
        return service.getSavedPaymentMethod(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/deactivate/{paymentMethodId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDTO> deactivatePaymentMethod(@PathVariable Long paymentMethodId) {
        service.deactivatePaymentMethodById(paymentMethodId);
        return ResponseEntity.ok(new ApiResponseDTO("success", "Payment method was successfully deleted."));
    }
}
