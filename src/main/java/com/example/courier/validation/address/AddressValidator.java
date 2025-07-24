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
        return street != null && patterns.street().matcher(street).matches();
    }

    public boolean isCityValid(String city) {
        return city != null && patterns.city().matcher(city).matches();
    }

    public boolean isHouseNumberValid(String houseNumber) {
        return houseNumber != null && patterns.houseNumber().matcher(houseNumber).matches();
    }

    public boolean isFlatNumberValid(String flatNumber) {
        return flatNumber != null && patterns.flatNumber().matcher(flatNumber).matches();
    }
}
