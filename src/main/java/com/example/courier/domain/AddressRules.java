package com.example.courier.validation.address;

import java.util.regex.Pattern;

public final class AddressValidator {

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[\\p{L}\\s\\-]{2,40}$");;
    private static final Pattern STREET_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-.]{3,60}$");
    private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z\\-/]{1,10}$");
    private static final Pattern FLAT_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z]{1,6}$");


    private AddressValidator() {}

    public static void isPostalCodeValid(String postalCode) {
        validate(postalCode, POSTAL_CODE_PATTERN, "Invalid postal code");
    }

    public static void isStreetValid(String street) {
        String s = normalize(street);
        validate(s, STREET_PATTERN, "Invalid street name");
    }

    public static void isCityValid(String city) {
        String c = normalize(city);
        validate(c, CITY_PATTERN, "Invalid city name");
    }

    public static void isHouseNumberValid(String houseNumber) {
        validate(houseNumber, HOUSE_NUMBER_PATTERN, "Invalid house number");
    }

    public static void isFlatNumberValid(String flatNumber) {
        if (flatNumber != null && !flatNumber.isEmpty()) {
            validate(flatNumber, FLAT_NUMBER_PATTERN, "Invalid flat number");
        }
    }

    /* Helper methods
    */

    private static void validate(String value, Pattern pattern, String message) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new ValidationException(message);
        }
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }
}
