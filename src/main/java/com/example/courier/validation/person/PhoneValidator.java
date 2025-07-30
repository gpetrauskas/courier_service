package com.example.courier.validation.person;

import com.example.courier.validation.PersonPatterns;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneValidator {
    private static final Logger logger = LoggerFactory.getLogger(PhoneValidator.class);
    private final PersonPatterns patterns;

    public PhoneValidator(PersonPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isValid(String phone) {
        return phone != null && patterns.phone().matcher(phone).matches();
    }

    public String validateAndFormat(String phone) throws ValidationException {
        logger.info("Running validateAndFormat with phone: {}", phone);
        if (!isValid(phone)) {
            throw new ValidationException("Phone must be 8 digits");
        }
        return format(phone);
    }

    public String format(String phone) {
        return "370" + phone.replaceAll("[^0-9]", "");
    }
}