package com.example.courier.service.validation.strategy.address;

import com.example.courier.common.AddressValidationMode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AddressValidationStrategyRegistry {
    private final Map<AddressValidationMode, AddressValidationStrategy> registry = new EnumMap<>(AddressValidationMode.class);

    public AddressValidationStrategyRegistry(
            CreateNewAddressValidator createNew,
            ExistingAddressValidator existing,
            UpdateAddressValidator update
    ) {
        registry.put(AddressValidationMode.CREATE_NEW, createNew);
        registry.put(AddressValidationMode.USE_EXISTING, existing);
        registry.put(AddressValidationMode.UPDATE, update);
    }

    public AddressValidationStrategy getStrategy(AddressValidationMode mode) {
        return registry.get(mode);
    }
}
