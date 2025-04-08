package com.example.courier.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    public void validatePassword(String password) {
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
