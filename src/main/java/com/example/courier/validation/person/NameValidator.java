package com.example.courier.validation.person;

import com.example.courier.validation.ValidationPatterns;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    private final ValidationPatterns patterns;

    public NameValidator(ValidationPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isValid(String fullName) {
        if (fullName == null || fullName.isBlank()) { return false; }

        String normalized = fullName.trim().replaceAll("\\s+", " ");
        return patterns.fullName().matcher(normalized).matches();
    }
}
