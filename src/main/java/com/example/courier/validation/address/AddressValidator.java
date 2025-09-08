package com.example.courier.validation.address;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AddressValidator {

    private final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private final Pattern CITY_PATTERN = Pattern.compile("^[\\p{L}\\s\\-]{2,40}$");;
    private final Pattern STREET_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-.]{3,60}$");
    private final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z\\-/]{1,10}$");
    private final Pattern FLAT_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z]{1,6}$");


    public AddressValidator() {}

    public boolean isPostalCodeValid(String postalCode) {
        return validate(postalCode, POSTAL_CODE_PATTERN, "Invalid postal code");
    }

    public boolean isStreetValid(String street) {
        String s = normalize(street);
        return validate(s, STREET_PATTERN, "Invalid street name");
    }

    public boolean isCityValid(String city) {
        String c = normalize(city);
        return validate(c, CITY_PATTERN, "Invalid city name");
    }

    public boolean isHouseNumberValid(String houseNumber) {
        return validate(houseNumber, HOUSE_NUMBER_PATTERN, "Invalid house number");
    }

    public boolean isFlatNumberValid(String flatNumber) {
        if (flatNumber != null && !flatNumber.isEmpty()) {
            return validate(flatNumber, FLAT_NUMBER_PATTERN, "Invalid flat number");
        }
        return true;
    }

    /* Helper methods
    */

    private boolean validate(String value, Pattern pattern, String message) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new ValidationException(message);
        }

        return true;
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }
}
