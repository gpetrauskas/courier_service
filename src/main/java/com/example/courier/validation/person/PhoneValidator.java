package com.example.courier.validation.person;

import com.example.courier.validation.ValidationPatterns;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private final ValidationPatterns patterns;

    public PhoneValidator(ValidationPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isValid(String phone) {
        return phone != null && patterns.phone().matcher(phone).matches();
    }

    public String validateAndFormat(String phone) throws ValidationException {
        if (!isValid(phone)) {
            throw new ValidationException("Phone must be 8 digits");
        }
        return format(phone);
    }

    public String format(String phone) {
        return "370" + phone.replaceAll("[^0-9]", "");
    }
}