package com.example.courier.controller;

import com.example.courier.domain.PricingOption;
import com.example.courier.dto.PricingOptionDTO;
import com.example.courier.service.pricingoption.PricingOptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing-options")
public class PricingOptionController {

    @Autowired
    private PricingOptionService pricingOptionService;

    @GetMapping
    public ResponseEntity<Map<String, List<PricingOption>>> getAllPricingOptions() {
        Map<String, List<PricingOption>> pricingOptions = pricingOptionService.getAllPricingOptions();
        return ResponseEntity.ok(pricingOptions);
    }

    @GetMapping("/notCategorized")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PricingOption>> getAllPricingOptionsNotCategorized() {
        List<PricingOption> list = pricingOptionService.getPricingOptionsNotCategorized();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updatePricingOption(@PathVariable Long id, @Valid @RequestBody PricingOptionDTO pricingOptionDTO) {
        pricingOptionService.updatePricingOption(id, pricingOptionDTO);

        return ResponseEntity.ok("Pricing option (id:" + id + ") successfully updated.");
    }


}
