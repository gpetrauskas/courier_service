package com.example.courier.service;

import com.example.courier.domain.PricingOption;
import com.example.courier.dto.OrderDTO;
import com.example.courier.exception.PricingOptionNotFoundException;
import com.example.courier.repository.PricingOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingOptionService {

    @Autowired
    private PricingOptionRepository pricingOptionRepository;

    public List<PricingOption> getAllPricingOptions() {
        return pricingOptionRepository.findAll();
    }

    @Transactional
    public BigDecimal calculateShippingCost(OrderDTO orderDTO) throws PricingOptionNotFoundException {
        BigDecimal shippingCost = new BigDecimal(0);

        BigDecimal deliveryPrice = getPriceById(orderDTO.deliveryPreferences());
        BigDecimal weightPrice = getPriceById(orderDTO.packageDetails().weight());
        BigDecimal sizePricing = getPriceById(orderDTO.packageDetails().dimensions());

        shippingCost = shippingCost.add(deliveryPrice).add(weightPrice).add(sizePricing);

        return shippingCost;
    }

    private BigDecimal getPriceById(String id) {
        return pricingOptionRepository.findById(Long.parseLong(id))
                .map(PricingOption::getPrice)
                .orElseThrow(() -> new RuntimeException("price by pricing options not found"));
    }

    public String getDescriptionById(String id) {
        return pricingOptionRepository.findById(Long.parseLong(id))
                .map(PricingOption::getDescription)
                .orElseThrow(() -> new RuntimeException("Pricing option not found."));
    }
}
