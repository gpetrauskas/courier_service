package com.example.courier.service.validation;

import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.validation.address.AddressValidator;
import com.example.courier.validation.shared.BaseValidator;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class AddressValidationServiceImpl extends BaseValidator implements AddressValidationService {
    private final AddressValidator addressValidator;

    public AddressValidationServiceImpl(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    @Override
    public void validatePostalCode(String postalCode) {
        if (!addressValidator.isPostalCodeValid(postalCode)) {
            throw new ValidationException("Postal code cannot be validated");
        }
    }

    @Override
    public void validateStreet(String street) {
        if (!addressValidator.isStreetValid(street)) {
            throw new ValidationException("Street format is not valid");
        }
    }

    @Override
    public void validateCity(String city) {
        if (!addressValidator.isCityValid(city)) {
            throw new ValidationException("City format is not valid");
        }
    }

    @Override
    public void validateHouseNumber(String houseNumber) {
        if (!addressValidator.isHouseNumberValid(houseNumber)) {
            throw new ValidationException("Check house number input");
        }
    }

    @Override
    public void validateFlatNumber(String flatNumber) {
        if (!addressValidator.isFlatNumberValid(flatNumber)) {
            throw new ValidationException("Check flat number input");
        }
    }

    @Override
    public List<ApiResponseDTO> validateAddress(AddressDTO addressDTO) {
        List<ApiResponseDTO> errors = new ArrayList<>();

        validateField(addressDTO.city(), "city error", "Invalid city format", addressValidator::isCityValid, errors);
        validateField(addressDTO.street(), "street error", "Invalid street format", addressValidator::isStreetValid, errors);
        validateField(addressDTO.postCode(), "post code error", "Invalid post code format", addressValidator::isPostalCodeValid, errors);
        validateField(addressDTO.flatNumber(), "flat number error", "Invalid flat number format", addressValidator::isFlatNumberValid, errors);
        validateField(addressDTO.houseNumber(), "house number error", "Invalid house number format", addressValidator::isHouseNumberValid, errors);

        return errors;
    }
}
