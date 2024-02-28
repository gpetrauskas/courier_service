package com.example.courier.service;

import com.example.courier.domain.PricingOption;
import com.example.courier.repository.PricingOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingOptionService {

    @Autowired
    private PricingOptionRepository pricingOptionRepository;

    public List<PricingOption> getAllPricingOptions() {
        return pricingOptionRepository.findAll();
    }
}
