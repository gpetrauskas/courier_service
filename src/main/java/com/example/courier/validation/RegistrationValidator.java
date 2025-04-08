package com.example.courier.validation;

import com.example.courier.dto.RegistrationDTO;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator {

    public void validateUserRegistration(RegistrationDTO dto) {
        validateEmail(dto.email());
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Email is not valid");
        }
    }

}
