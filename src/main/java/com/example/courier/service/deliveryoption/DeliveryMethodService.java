package com.example.courier.service.deliveryoption;

import com.example.courier.domain.DeliveryMethod;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.mapper.DeliveryMethodMapper;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;
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
public class DeliveryMethodService {

    private static final String DELIVERY_KEYWORD = "delivery";
    private static final String SIZE_KEYWORD = "size";
    private static final String WEIGHT_KEYWORD = "weight";
    private static final String PREFERENCE_KEYWORD = "preference";

    private final DeliveryOptionRepository deliveryOptionRepository;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final DeliveryOptionValidator validator;

    public DeliveryMethodService(
            DeliveryOptionRepository deliveryOptionRepository,
            DeliveryMethodMapper deliveryMethodMapper,
            DeliveryOptionValidator validator) {
        this.deliveryOptionRepository = deliveryOptionRepository;
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.validator = validator;
    }

    public Map<String, List<DeliveryMethodDTO>> getAllDeliveryOptions() {
        List<DeliveryMethod> allOptions =  deliveryOptionRepository.findAll();
        boolean isAdmin = AuthUtils.isAdmin();

        return allOptions
                .stream()
                .map(option -> isAdmin ?
                        deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(option) :
                        deliveryMethodMapper.toUserDeliveryOptionResponseDTO(option))
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

    public List<DeliveryMethodAdminResponseDTO> getDeliveryOptionsNotCategorized() {
        List<DeliveryMethod> list = deliveryOptionRepository.findAll();
        if (list.isEmpty()) {
            return List.of();
        }

        return list.stream()
                .map(deliveryMethodMapper::toAdminDeliveryOptionResponseDTO)
                .toList();
    }

    public void updateDeliveryOption(Long id, UpdateDeliveryMethodDTO deliveryOptionDTO) {
        DeliveryMethod existingDeliveryMethod = getDeliveryOptionById(id);
        deliveryMethodMapper.updateDeliveryOptionFromDTO(deliveryOptionDTO, existingDeliveryMethod);
        deliveryOptionRepository.save(existingDeliveryMethod);
    }

    @Transactional
    public void addNewDeliveryOption(CreateDeliveryMethodDTO createDeliveryMethodDTO) {
        existsByName(createDeliveryMethodDTO.name());
        validator.validateDeliveryOptionForCreation(createDeliveryMethodDTO);
        DeliveryMethod deliveryMethod = deliveryMethodMapper.toNewEntity(createDeliveryMethodDTO);
        deliveryOptionRepository.save(deliveryMethod);
    }

    @Transactional
    public void deleteDeliveryOption(Long id) {
        if (!deliveryOptionRepository.existsById(id)) {
            throw new DeliveryOptionNotFoundException("No delivery option with such id");
        }

        deliveryOptionRepository.deleteById(id);
    }

    public DeliveryMethodDTO getById(Long id) {
        DeliveryMethod option = deliveryOptionRepository.findById(id)
                .orElseThrow(() -> new DeliveryOptionNotFoundException("No delivery method with ID: " + id));

        return deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(option);
    }






    @Transactional
    public BigDecimal calculateShippingCost(OrderDTO orderDTO) throws DeliveryOptionNotFoundException {
        BigDecimal shippingCost = new BigDecimal(0);

        BigDecimal deliveryPrice = getPriceById(orderDTO.deliveryMethod());
        BigDecimal weightPrice = getPriceById(orderDTO.parcelDetails().weight());
        BigDecimal sizePricing = getPriceById(orderDTO.parcelDetails().dimensions());

        shippingCost = shippingCost.add(deliveryPrice).add(weightPrice).add(sizePricing);

        return shippingCost;
    }

    private BigDecimal getPriceById(String id) {
        return deliveryOptionRepository.findById(Long.parseLong(id))
                .map(DeliveryMethod::getPrice)
                .orElseThrow(() -> new RuntimeException("price by delivery options not found"));
    }

    private DeliveryMethod getDeliveryOptionById(Long id) {
        return deliveryOptionRepository.findById(id).orElseThrow(() ->
                new DeliveryOptionNotFoundException("Delivery option was not found with id " + id));
    }

    public String getDescriptionById(String id) {
        return deliveryOptionRepository.findById(Long.parseLong(id))
                .map(DeliveryMethod::getDescription)
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
