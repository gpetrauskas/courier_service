package com.example.courier.service.validation;

import com.example.courier.common.AddressValidationMode;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.exception.CompositeValidationException;
import com.example.courier.service.validation.strategy.address.AddressValidationStrategyRegistry;
import com.example.courier.validation.shared.BaseValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AddressValidationServiceImpl extends BaseValidator implements AddressValidationService {
    private final AddressValidationStrategyRegistry strategyRegistry;

    public AddressValidationServiceImpl(AddressValidationStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    @Override
    public void validateAddress(AddressDTO addressDTO, AddressValidationMode mode) {
        Objects.requireNonNull(addressDTO, "AddressDTO cannot be null");
        List<ApiResponseDTO> errors = new ArrayList<>();
        strategyRegistry.getStrategy(mode).validate(addressDTO, errors);

        if (!errors.isEmpty()) {
            throw new CompositeValidationException(errors);
        }
    }
}
