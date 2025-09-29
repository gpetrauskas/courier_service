package com.example.courier.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record AddressDetails(
    String name,
    String street,
    String houseNumber,
    String flatNumber,
    String city,
    String postCode,
    String phoneNumber
) {
    public AddressDetails {
        AddressRules.validateName(name);
        AddressRules.validateStreet(street);
        AddressRules.validateHouseNumber(houseNumber);
        AddressRules.validateFlatNumber(flatNumber);
        AddressRules.validateCity(city);
        AddressRules.validatePostCode(postCode);
        AddressRules.validatePhone(phoneNumber);
    }
}
