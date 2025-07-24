package com.example.courier.config;

import com.example.courier.validation.PersonPatterns;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class RegexConfig implements PersonPatterns {

    @Bean
    @Override
    public Pattern phone() {
        return Pattern.compile("^[0-9]{8}$");
    }

    @Bean
    @Override
    public Pattern email() {
        return Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$");
    }

    @Bean
    @Override
    public Pattern fullName() {
        return Pattern.compile("^[A-Za-z]{2,20} [A-Za-z]{2,30}$");
    }
}
