package com.example.courier.service.pricingoption;

import com.example.courier.common.PricingOptionCategory;
import com.example.courier.domain.PricingOption;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PricingOptionDTO;
import com.example.courier.dto.mapper.PricingOptionMapper;
import com.example.courier.exception.PricingOptionNotFoundException;
import com.example.courier.repository.PricingOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PricingOptionService {

    private static final String DELIVERY_KEYWORD = "delivery";
    private static final String SIZE_KEYWORD = "size";
    private static final String WEIGHT_KEYWORD = "weight";
    private static final String PREFERENCE_KEYWORD = "preference";

    private final PricingOptionRepository pricingOptionRepository;
    private final PricingOptionMapper pricingOptionMapper;

    public PricingOptionService(PricingOptionRepository pricingOptionRepository, PricingOptionMapper pricingOptionMapper) {
        this.pricingOptionRepository = pricingOptionRepository;
        this.pricingOptionMapper = pricingOptionMapper;
    }

    public Map<String, List<PricingOption>> getAllPricingOptions() {
        List<PricingOption> allOptions =  pricingOptionRepository.findAll();

        return allOptions
                .stream()
                .collect(Collectors.groupingBy(option -> {
                    if (option.getName().contains(WEIGHT_KEYWORD)) {
                        return WEIGHT_KEYWORD;
                    } else if (option.getName().contains(SIZE_KEYWORD)) {
                        return SIZE_KEYWORD;
                    } else {
                        return PREFERENCE_KEYWORD;
                    }
                }));
    }

    public List<PricingOption> getPricingOptionsNotCategorized() {
        List<PricingOption> list = pricingOptionRepository.findAll();
        if (list.isEmpty()) {
            return List.of();
        }

        return list;
    }

    public void updatePricingOption(Long id, PricingOptionDTO pricingOptionDTO) {
        PricingOption existingPricingOption = getPricingOptionById(id);
        pricingOptionMapper.updatePricingOptionFromDTO(pricingOptionDTO, existingPricingOption);
        pricingOptionRepository.save(existingPricingOption);
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

    private PricingOption getPricingOptionById(Long id) {
        return pricingOptionRepository.findById(id).orElseThrow(() ->
                new PricingOptionNotFoundException("Pricing option was not found with id " + id));
    }

    public String getDescriptionById(String id) {
        return pricingOptionRepository.findById(Long.parseLong(id))
                .map(PricingOption::getDescription)
                .orElseThrow(() -> new RuntimeException("Pricing option not found."));
    }

    @Cacheable("deliveryPreferences")
    public Set<String> getDeliveryPreferences() {
        return new HashSet<>(pricingOptionRepository.findDeliveryPreferences(DELIVERY_KEYWORD));
    }
}
