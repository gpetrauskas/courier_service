package com.example.courier.service.validation.strategy.address;

import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.validation.person.NameValidator;
import com.example.courier.validation.person.PhoneValidator;
import com.example.courier.validation.shared.BaseValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExistingAddressValidator extends BaseValidator implements AddressValidationStrategy {
    private final NameValidator nameValidator;
    private final PhoneValidator phoneValidator;

    public ExistingAddressValidator(NameValidator nameValidator, PhoneValidator phoneValidator) {
        this.nameValidator = nameValidator;
        this.phoneValidator = phoneValidator;
    }

    @Override
    public void validate(AddressDTO dto, List<ApiResponseDTO> errors) {
        if (dto.id() == null) {
            errors.add(new ApiResponseDTO("id", "Address ID must be provided"));
        }
        validateRequiredField(dto.name(), "name", "Invalid name format", nameValidator::isValid, errors);
        validateRequiredField(dto.phoneNumber(), "phone", "Invalid phone format", phoneValidator::isValid, errors);
    }
}
