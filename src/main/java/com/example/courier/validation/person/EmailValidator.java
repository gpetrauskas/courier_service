package com.example.courier.validation.person;

import com.example.courier.validation.PersonPatterns;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    private final PersonPatterns patterns;

    public EmailValidator(PersonPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isValid(String email) {
        return email != null && patterns.email().matcher(email).matches();
    }



}
