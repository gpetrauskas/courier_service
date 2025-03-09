package com.example.courier.validation;

import com.example.courier.domain.Order;
import com.example.courier.dto.request.OrderSectionUpdateRequest;
import com.example.courier.exception.InvalidDeliveryPreferenceException;
import com.example.courier.service.pricingoption.PricingOptionService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PricingOptionValidator {

    private final PricingOptionService pricingOptionService;

    public PricingOptionValidator(PricingOptionService pricingOptionService) {
        this.pricingOptionService = pricingOptionService;
    }

    public void validateDeliveryPrefForOrderStatusUpdate(OrderSectionUpdateRequest updateRequest, Order existingOrder) {
        validateDeliveryPreference(updateRequest.deliveryPreferences(), existingOrder.getDeliveryPreferences());
    }

    private void validateDeliveryPreference(String newPreference, String currentPreference) {
        if (newPreference != null && newPreference.equalsIgnoreCase(currentPreference)) {
            throw new IllegalArgumentException("Delivery preference is already set to: " + newPreference);
        }
        if (newPreference != null) {
            Set<String> validPreferences = pricingOptionService.getDeliveryPreferences();
            if (!validPreferences.contains(newPreference)) {
                throw new InvalidDeliveryPreferenceException("Invalid delivery preference");
            }
        }
    }
}
