package com.example.courier.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");

    public String validate(String phone) throws ValidationException {
        if (phone == null) return null;

        String digits = phone.replaceAll("[^0-9]", "");

        if (!PHONE_PATTERN.matcher(digits).matches()) {
            throw new ValidationException("Phone must contain exactly 8 digits");
        }

        return "370" + digits;
    }
}