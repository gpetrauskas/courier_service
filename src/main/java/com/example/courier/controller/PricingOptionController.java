package com.example.courier.controller;

import com.example.courier.domain.PricingOption;
import com.example.courier.service.PricingOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
