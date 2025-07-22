package com.example.courier.validation.person;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.CompositeValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public final class PersonDetailsValidator {
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

        validateField(request.name(), "name error", "Invalid full name", nameValidator::isValid, errors);
        validateField(request.email(), "email error", "Invalid email", emailValidator::isValid, errors);
        validateField(request.phoneNumber(), "phone error", "Invalid phone number", phoneValidator::isValid, errors);

        if (!errors.isEmpty()) {
            throw new CompositeValidationException(errors);
        }
    }

    public void validatePhone(String phone) {
        if (phone != null && !phoneValidator.isValid(phone)) {
            throw new CompositeValidationException(List.of(new ApiResponseDTO("phone error", "Invalid phone number")));
        }
    }

    private void validateField(String value, String code, String message, Predicate<String> validator, List<ApiResponseDTO> errors) {
        Optional.ofNullable(value)
                .filter(v -> !v.isBlank())
                .filter(v -> !validator.test(v))
                .ifPresent(v -> errors.add(new ApiResponseDTO(code, message)));
    }
}
