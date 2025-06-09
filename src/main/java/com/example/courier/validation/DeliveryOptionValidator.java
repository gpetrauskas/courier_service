package com.example.courier.validation;

import com.example.courier.domain.Order;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class DeliveryOptionValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("^[a-zA-Z0-9 .,!?-]+$");

    public DeliveryOptionValidator() {
    }

    public void validateDeliveryPrefMethodUpdate(OrderSectionUpdateRequest updateRequest, Order existingOrder) {
        String newPreference = updateRequest.deliveryPreferences();
        String currentPreference = existingOrder.getDeliveryMethod();

        if (newPreference.equalsIgnoreCase(currentPreference)) {
            throw new ValidationException("Delivery preference is already set to:" + currentPreference);
        }

        //validateDeliveryPreference(newPreference);
    }

    public void validateDeliveryOptionForCreation(CreateDeliveryMethodDTO createDeliveryMethodDTO) {
        validateString(createDeliveryMethodDTO.name(), "name", 20, NAME_PATTERN, "can opnly contain letters");
        validateString(createDeliveryMethodDTO.description(), "description", 40, DESCRIPTION_PATTERN, "contains invalid characters");
        validatePositive(createDeliveryMethodDTO.price(), "price");
    }

    private void validateString(String value, String fieldName, int maxLength, Pattern pattern, String errorMsg) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be null or empty.");
        }
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName + " cannot exceed " + maxLength + " characters");
        }
        if (!pattern.matcher(value).matches()) {
            throw new ValidationException(fieldName + " " + errorMsg);
        }
    }

    private void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " must be positive value.");
        }
    }

/*    private void validateDeliveryPreference(String newPreference) {
        Set<String> validPreferences = deliveryOptionService.getDeliveryPreferences();
        if (!validPreferences.contains(newPreference)) {
            throw new InvalidDeliveryPreferenceException("Invalid delivery preference");
        }
    }*/

}
