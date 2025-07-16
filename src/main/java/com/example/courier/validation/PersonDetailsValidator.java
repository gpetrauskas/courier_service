package com.example.courier.validation;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.CompositeValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public final class PersonDetailsValidator {
    private final EmailValidator emailValidator;
    private final PhoneValidator phoneValidator;

    public PersonDetailsValidator(EmailValidator emailValidator, PhoneValidator phoneValidator) {
        this.emailValidator = emailValidator;
        this.phoneValidator = phoneValidator;
    }

    public void validate(PersonDetailsUpdateRequest request) {
        List<ApiResponseDTO> errors = new ArrayList<>();

        Optional.ofNullable(request.email())
                .filter(e -> !emailValidator.isValid(e))
                .ifPresent(e -> errors.add(new ApiResponseDTO("email error", "Invalid email")));

        Optional.ofNullable(request.phoneNumber())
                .filter(e -> !phoneValidator.isValid(e))
                .ifPresent(e -> errors.add(new ApiResponseDTO("phone error", "Invalid phone number")));

        if (!errors.isEmpty()) {
            throw new CompositeValidationException(errors);
        }
    }
}
