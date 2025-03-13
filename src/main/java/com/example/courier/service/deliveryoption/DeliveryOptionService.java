package com.example.courier.service.deliveryoption;

import com.example.courier.domain.DeliveryOption;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.mapper.DeliveryOptionMapper;
import com.example.courier.dto.request.deliveryoption.CreateDeliveryOptionDTO;
import com.example.courier.dto.request.deliveryoption.UpdateDeliveryOptionDTO;
import com.example.courier.dto.response.deliveryoption.DeliveryOptionDTO;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.repository.DeliveryOptionRepository;
import com.example.courier.util.AuthUtils;
import com.example.courier.validation.DeliveryOptionValidator;
import jakarta.validation.ValidationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryOptionService {

    private static final String DELIVERY_KEYWORD = "delivery";
    private static final String SIZE_KEYWORD = "size";
    private static final String WEIGHT_KEYWORD = "weight";
    private static final String PREFERENCE_KEYWORD = "preference";

    private final DeliveryOptionRepository deliveryOptionRepository;
    private final DeliveryOptionMapper deliveryOptionMapper;
    private final DeliveryOptionValidator validator;

    public DeliveryOptionService(
            DeliveryOptionRepository deliveryOptionRepository,
            DeliveryOptionMapper deliveryOptionMapper,
            DeliveryOptionValidator validator) {
        this.deliveryOptionRepository = deliveryOptionRepository;
        this.deliveryOptionMapper = deliveryOptionMapper;
        this.validator = validator;
    }

    public Map<String, List<DeliveryOptionDTO>> getAllDeliveryOptions() {
        List<DeliveryOption> allOptions =  deliveryOptionRepository.findAll();
        boolean isAdmin = AuthUtils.isAdmin();

        return allOptions
                .stream()
                .map(option -> isAdmin ?
                        deliveryOptionMapper.toAdminDeliveryOptionResponseDTO(option) :
                        deliveryOptionMapper.toUserDeliveryOptionResponseDTO(option))
                .collect(Collectors.groupingBy(option -> {
                    if (option.name().contains(WEIGHT_KEYWORD)) {
                        return WEIGHT_KEYWORD;
                    } else if (option.name().contains(SIZE_KEYWORD)) {
                        return SIZE_KEYWORD;
                    } else {
                        return PREFERENCE_KEYWORD;
                    }
                }));
    }

    public List<DeliveryOption> getDeliveryOptionsNotCategorized() {
        List<DeliveryOption> list = deliveryOptionRepository.findAll();
        if (list.isEmpty()) {
            return List.of();
        }

        return list;
    }

    public void updateDeliveryOption(Long id, UpdateDeliveryOptionDTO deliveryOptionDTO) {
        DeliveryOption existingDeliveryOption = getDeliveryOptionById(id);
        deliveryOptionMapper.updateDeliveryOptionFromDTO(deliveryOptionDTO, existingDeliveryOption);
        deliveryOptionRepository.save(existingDeliveryOption);
    }

    @Transactional
    public void addNewDeliveryOption(CreateDeliveryOptionDTO createDeliveryOptionDTO) {
        existsByName(createDeliveryOptionDTO.name());
        validator.validateDeliveryOptionForCreation(createDeliveryOptionDTO);
        DeliveryOption deliveryOption = deliveryOptionMapper.toNewEntity(createDeliveryOptionDTO);
        deliveryOptionRepository.save(deliveryOption);
    }

    @Transactional
    public void deleteDeliveryOption(Long id) {
        if (!deliveryOptionRepository.existsById(id)) {
            throw new DeliveryOptionNotFoundException("No delivery option with such id");
        }

        deliveryOptionRepository.deleteById(id);
    }






    @Transactional
    public BigDecimal calculateShippingCost(OrderDTO orderDTO) throws DeliveryOptionNotFoundException {
        BigDecimal shippingCost = new BigDecimal(0);

        BigDecimal deliveryPrice = getPriceById(orderDTO.deliveryPreferences());
        BigDecimal weightPrice = getPriceById(orderDTO.parcelDetails().weight());
        BigDecimal sizePricing = getPriceById(orderDTO.parcelDetails().dimensions());

        shippingCost = shippingCost.add(deliveryPrice).add(weightPrice).add(sizePricing);

        return shippingCost;
    }

    private BigDecimal getPriceById(String id) {
        return deliveryOptionRepository.findById(Long.parseLong(id))
                .map(DeliveryOption::getPrice)
                .orElseThrow(() -> new RuntimeException("price by delivery options not found"));
    }

    private DeliveryOption getDeliveryOptionById(Long id) {
        return deliveryOptionRepository.findById(id).orElseThrow(() ->
                new DeliveryOptionNotFoundException("Delivery option was not found with id " + id));
    }

    public String getDescriptionById(String id) {
        return deliveryOptionRepository.findById(Long.parseLong(id))
                .map(DeliveryOption::getDescription)
                .orElseThrow(() -> new RuntimeException("Delivery option not found."));
    }

    @Cacheable("deliveryMethod")
    public Set<String> getDeliveryPreferences() {
        return new HashSet<>(deliveryOptionRepository.findDeliveryPreferences(DELIVERY_KEYWORD));
    }

    private void existsByName(String name) {
        if (deliveryOptionRepository.existsByName(name)) {
            throw new ValidationException("Delivery option with " + name + " already exists");
        }
    }
}
