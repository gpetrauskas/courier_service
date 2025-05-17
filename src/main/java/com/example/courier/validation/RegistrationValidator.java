package com.example.courier.validation;

import com.example.courier.dto.RegistrationDTO;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class RegistrationValidator {

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^(?=.{1,64}@)[a-zA-Z0-9](?!.*\\.\\.)[a-zA-Z0-9._%+-]{0,63}@" +
                    "(?!-)[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,63}$"
    );

    public void validateUserRegistration(RegistrationDTO dto) {
        validateRegistration(dto);
    }

    private void validateEmail(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new ValidationException("Email is not valid");
        }
    }

    private void validateRegistration(RegistrationDTO dto) {
        Objects.requireNonNull(dto, "RegistrationDTO cannot be null");
        validateEmail(dto.email());
    }

}
