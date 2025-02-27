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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingOptionService {

    private static final String DELIVERY_KEYWORD = "delivery";

    @Autowired
    private PricingOptionRepository pricingOptionRepository;

    public Map<String, List<PricingOption>> getAllPricingOptions() {
        List<PricingOption> allOptions =  pricingOptionRepository.findAll();

        Map<String, List<PricingOption>> categorizedOptions = allOptions
                .stream()
                .collect(Collectors.groupingBy(option -> {
                    if (option.getName().contains("weight")) {
                        return "weight";
                    } else if (option.getName().contains("size")) {
                        return "size";
                    } else {
                        return "preference";
                    }
                }));

        return categorizedOptions;
    }

    public List<PricingOption> getPricingOptionsNotCategorized() {
        List<PricingOption> list = pricingOptionRepository.findAll();

        return list;
    }

    @Transactional
    public BigDecimal calculateShippingCost(OrderDTO orderDTO) throws PricingOptionNotFoundException {
        BigDecimal shippingCost = new BigDecimal(0);

        BigDecimal deliveryPrice = getPriceById(orderDTO.deliveryPreferences());
        BigDecimal weightPrice = getPriceById(orderDTO.parcelDetails().weight());
        BigDecimal sizePricing = getPriceById(orderDTO.parcelDetails().dimensions());

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

    public List<String> getDeliveryPreferences() {
        return pricingOptionRepository.findAll().stream()
                .filter(o -> o.getDescription().contains(DELIVERY_KEYWORD))
                .map(PricingOption::getDescription)
                .toList();
    }
}
