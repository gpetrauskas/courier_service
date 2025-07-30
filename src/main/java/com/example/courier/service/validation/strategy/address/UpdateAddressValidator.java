package com.example.courier.service.validation.strategy.address;

import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.validation.address.AddressValidator;
import com.example.courier.validation.person.NameValidator;
import com.example.courier.validation.person.PhoneValidator;
import com.example.courier.validation.shared.BaseValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateAddressValidator extends BaseValidator implements AddressValidationStrategy {
    private final AddressValidator addressValidator;
    private final PhoneValidator phoneValidator;
    private final NameValidator nameValidator;

    public UpdateAddressValidator(AddressValidator addressValidator, PhoneValidator phoneValidator,
                                  NameValidator nameValidator) {
        this.addressValidator = addressValidator;
        this.phoneValidator = phoneValidator;
        this.nameValidator = nameValidator;
    }

    @Override
    public void validate(AddressDTO dto, List<ApiResponseDTO> errors) {
        validateOptionalField(dto.name(), "name", "Invalid name format", nameValidator::isValid, errors);
        validateOptionalField(dto.city(), "city", "Invalid city format", addressValidator::isCityValid, errors);
        validateOptionalField(dto.street(), "street", "Invalid street format", addressValidator::isStreetValid, errors);
        validateOptionalField(dto.postCode(), "postCode", "Invalid post code format", addressValidator::isPostalCodeValid, errors);
        validateOptionalField(dto.houseNumber(), "houseNumber", "Invalid house number format", addressValidator::isHouseNumberValid, errors);
        validateOptionalField(dto.phoneNumber(), "phone", "Invalid phone format", phoneValidator::isValid, errors);
        validateOptionalField(dto.flatNumber(), "flatNumber", "Invalid flat number format", addressValidator::isFlatNumberValid, errors);
    }
}
