package com.example.courier.config.validation;

import com.example.courier.validation.PersonPatterns;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class PersonPatternConfig implements PersonPatterns {
    private final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$");
    private final Pattern FULLNAME_PATTERN = Pattern.compile("^[A-Za-z]{2,20} [A-Za-z]{2,30}$");

    @Bean
    public Pattern phone() {
        return PHONE_PATTERN;
    }

    @Bean
    public Pattern email() {
        return EMAIL_PATTERN;
    }

    @Bean
    public Pattern fullName() {
        return FULLNAME_PATTERN;
    }
}
