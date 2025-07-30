package com.example.courier.validation.person;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.CompositeValidationException;
import com.example.courier.validation.shared.BaseValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class PersonDetailsValidator extends BaseValidator {
    private final EmailValidator emailValidator;
    private final PhoneValidator phoneValidator;
    private final NameValidator nameValidator;

    public PersonDetailsValidator(EmailValidator emailValidator, PhoneValidator phoneValidator, NameValidator nameValidator) {
        this.emailValidator = emailValidator;
        this.phoneValidator = phoneValidator;
        this.nameValidator = nameValidator;
    }

    public void validate(PersonDetailsUpdateRequest request) {
        List<ApiResponseDTO> errors = new ArrayList<>();

        validateOptionalField(request.name(), "name error", "Invalid full name", nameValidator::isValid, errors);
        validateOptionalField(request.email(), "email error", "Invalid email", emailValidator::isValid, errors);
        validateOptionalField(request.phoneNumber(), "phone error", "Invalid phone number", phoneValidator::isValid, errors);

        if (!errors.isEmpty()) {
            throw new CompositeValidationException(errors);
        }
    }
}
