package com.example.courier.validation.address;

import com.example.courier.validation.AddressPatterns;
import org.springframework.stereotype.Component;

@Component
public class AddressValidator {
    private final AddressPatterns patterns;

    public AddressValidator(AddressPatterns patterns) {
        this.patterns = patterns;
    }

    public boolean isPostalCodeValid(String postalCode) {
        return postalCode != null && patterns.postCode().matcher(postalCode).matches();
    }

    public boolean isStreetValid(String street) {
        String s = normalize(street);
        return s != null && patterns.street().matcher(s).matches();
    }

    public boolean isCityValid(String city) {
        String c = normalize(city);
        return c != null && patterns.city().matcher(c).matches();
    }

    public boolean isHouseNumberValid(String houseNumber) {
        return houseNumber != null && patterns.houseNumber().matcher(houseNumber).matches();
    }

    public boolean isFlatNumberValid(String flatNumber) {
        return flatNumber != null && patterns.flatNumber().matcher(flatNumber).matches();
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }
}
