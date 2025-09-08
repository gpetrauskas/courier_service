package com.example.courier.service.deliveryoption;

import com.example.courier.common.DeliveryGroup;
import com.example.courier.domain.DeliveryMethod;
import com.example.courier.domain.Order;
import com.example.courier.dto.mapper.DeliveryMethodMapper;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.repository.DeliveryOptionRepository;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.deliveryoption.DeliveryOptionValidator;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryMethodServiceImpl implements DeliveryMethodService {

    private static final String DELIVERY_KEYWORD = "delivery";
    private static final Logger log = LoggerFactory.getLogger(DeliveryMethodServiceImpl.class);

    private final DeliveryOptionRepository deliveryOptionRepository;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final DeliveryOptionValidator validator;
    private final CurrentPersonService currentPersonService;

    public DeliveryMethodServiceImpl(
            DeliveryOptionRepository deliveryOptionRepository,
            DeliveryMethodMapper deliveryMethodMapper,
            DeliveryOptionValidator validator,
            CurrentPersonService currentPersonService) {
        this.deliveryOptionRepository = deliveryOptionRepository;
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.validator = validator;
        this.currentPersonService = currentPersonService;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<DeliveryGroup, List<DeliveryMethodDTO>> getAllDeliveryOptions() {
        boolean isAdmin = currentPersonService.isAdmin();
        List<DeliveryMethod> allOptions = getAll();
        return allOptions.stream()
                .filter(option -> isAdmin || !option.isDisabled())
                .map(option -> mapDeliveryOption(option, isAdmin))
                .collect(Collectors.groupingBy(this::determineDeliveryGroup));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryMethodAdminResponseDTO> getDeliveryOptionsNotCategorized() {
        return getAll().stream()
                .map(deliveryMethodMapper::toAdminDeliveryOptionResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void updateDeliveryOption(Long id, UpdateDeliveryMethodDTO deliveryOptionDTO) {
        DeliveryMethod existingDeliveryMethod = getDeliveryOptionById(id);
        deliveryMethodMapper.updateDeliveryOptionFromDTO(deliveryOptionDTO, existingDeliveryMethod);
        deliveryOptionRepository.save(existingDeliveryMethod);
    }

    @Override
    @Transactional
    public void addNewDeliveryOption(CreateDeliveryMethodDTO createDeliveryMethodDTO) {
        validateNameUnique(createDeliveryMethodDTO.name());
        validator.validateDeliveryOptionForCreation(createDeliveryMethodDTO);
        DeliveryMethod deliveryMethod = deliveryMethodMapper.toNewEntity(createDeliveryMethodDTO);
        deliveryOptionRepository.save(deliveryMethod);
    }

    @Override
    @Transactional
    public void deleteDeliveryOption(Long id) {
        if (!deliveryOptionRepository.existsById(id)) {
            throw new DeliveryOptionNotFoundException("No delivery option with such id");
        }

        deliveryOptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryMethodDTO getAdminDeliveryOptionById(Long id) {
        DeliveryMethod option = deliveryOptionRepository.findById(id)
                .orElseThrow(() -> new DeliveryOptionNotFoundException("No delivery method with ID: " + id));

        return deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(option);
    }

    @Override
    @Transactional
    public BigDecimal calculateShippingCost(Order order) throws DeliveryOptionNotFoundException {
        Set<Long> deliveryOptionIds = Set.of(
                order.getPreference().getId(),
                order.getParcelDetails().getWeight().getId(),
                order.getParcelDetails().getDimensions().getId()
        );

        List<BigDecimal> priceList = getAllPricesById(deliveryOptionIds);
        if (priceList.size() != deliveryOptionIds.size()) {
            throw new DeliveryOptionNotFoundException("One or more delivery options not found for ids: " + deliveryOptionIds);
        }

        return priceList.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryMethod getDeliveryOptionById(Long id) {
        return deliveryOptionRepository.findById(id).orElseThrow(() ->
                new DeliveryOptionNotFoundException("Delivery option was not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public String getDescriptionById(Long id) {
        return deliveryOptionRepository.findById(id)
                .map(DeliveryMethod::getDescription)
                .orElseThrow(() -> new DeliveryOptionNotFoundException("Delivery option not found."));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("preference")
    public Set<String> getDeliveryPreferences() {
        return new HashSet<>(deliveryOptionRepository.findDeliveryPreferences(DELIVERY_KEYWORD));
    }

    /* Helper methods
    */

    private void validateNameUnique(String name) {
        if (deliveryOptionRepository.existsByName(name)) {
            throw new ValidationException("Delivery option with " + name + " already exists");
        }
    }

    private List<BigDecimal> getAllPricesById(Set<Long> ids) {
        return deliveryOptionRepository.findAllById(ids).stream()
                .map(DeliveryMethod::getPrice)
                .toList();
    }

    private List<DeliveryMethod> getAll() {
        return deliveryOptionRepository.findAll();
    }


    private DeliveryMethodDTO mapDeliveryOption(DeliveryMethod option, boolean isAdmin) {
        return (isAdmin)
                ? deliveryMethodMapper.toAdminDeliveryOptionResponseDTO(option)
                : deliveryMethodMapper.toUserDeliveryOptionResponseDTO(option);
    }

    private DeliveryGroup determineDeliveryGroup(DeliveryMethodDTO dto) {
        return DeliveryGroup.determineGroupFromName(dto.name());
    }
}
