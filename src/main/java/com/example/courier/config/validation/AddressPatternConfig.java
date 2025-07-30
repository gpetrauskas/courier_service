package com.example.courier.config.validation;

import com.example.courier.validation.AddressPatterns;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class AddressPatternConfig implements AddressPatterns {

    private final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private final Pattern CITY_PATTERN = Pattern.compile("^[\\p{L}]+(?:[\\s-][\\p{L}]+)*$");
    private final Pattern STREET_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-.]{3,60}$");
    private final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z\\-/]{1,10}$");
    private final Pattern FLAT_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z]{1,6}$");

    @Bean
    public Pattern postCode() {
        return POSTAL_CODE_PATTERN;
    }

    @Bean
    public Pattern city() {
        return CITY_PATTERN;
    }

    @Bean
    public Pattern street() {
        return STREET_PATTERN;
    }

    @Bean
    public Pattern houseNumber() {
        return HOUSE_NUMBER_PATTERN;
    }

    @Bean
    public Pattern flatNumber() {
        return FLAT_NUMBER_PATTERN;
    }
}
