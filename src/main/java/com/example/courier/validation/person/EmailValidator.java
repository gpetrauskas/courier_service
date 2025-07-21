package com.example.courier.validation;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    private final ValidationPatterns patterns;

    public EmailValidator(ValidationPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isValid(String email) {
        return email != null && patterns.email().matcher(email).matches();
    }



}
