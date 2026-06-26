package gytis.courier.domain.address;

import gytis.courier.exception.InvalidAddressException;

import java.util.regex.Pattern;

public final class AddressRules {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern FULLNAME_PATTERN = Pattern.compile("^[A-Za-z]{2,20} [A-Za-z]{2,30}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[\\p{L}\\s\\-]{2,40}$");;
    private static final Pattern STREET_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-.]{3,60}$");
    private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z\\-/]{1,10}$");
    private static final Pattern FLAT_NUMBER_PATTERN = Pattern.compile("^[0-9A-Za-z]{1,6}$");


    private AddressRules() {}

    public static void validatePostCode(String postalCode) {
        validate(postalCode, POSTAL_CODE_PATTERN, "Invalid postal code");
    }

    public static void validateStreet(String street) {
        String s = normalize(street);
        validate(s, STREET_PATTERN, "Invalid street name");
    }

    public static void validateCity(String city) {
        String c = normalize(city);
        validate(c, CITY_PATTERN, "Invalid city name");
    }

    public static void validateHouseNumber(String houseNumber) {
        validate(houseNumber, HOUSE_NUMBER_PATTERN, "Invalid house number");
    }

    public static void validateFlatNumber(String flatNumber) {
        if (flatNumber != null && !flatNumber.isEmpty()) {
            validate(flatNumber, FLAT_NUMBER_PATTERN, "Invalid flat number");
        }
    }

    public static void validatePhone(String phoneNumber) {
        String p = normalize(phoneNumber);
        validate(p, PHONE_PATTERN, "Invalid phone number");
    }

    public static void validateName(String name) {
        String n = normalize(name);
        validate(n, FULLNAME_PATTERN, "Invalid name");
    }

    /* Helper methods
    */

    private static void validate(String value, Pattern pattern, String message) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new InvalidAddressException(message);
        }
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }
}
