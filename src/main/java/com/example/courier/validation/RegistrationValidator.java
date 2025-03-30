package com.example.courier.validation;

import com.example.courier.dto.RegistrationDTO;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator {

    public void validateUserRegistration(RegistrationDTO dto) {
        validateEmail(dto.email());
        validatePassword(dto.password());
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Email is not valid");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 16) {
            throw new ValidationException("Password length must be between 8-16 characters");
        } if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        } if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Passwprd must contain at least one lowercase letter");
        } if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }
}
