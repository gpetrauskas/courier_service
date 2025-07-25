package com.example.courier.service.validation;

import com.example.courier.dto.AddressDTO;

public interface AddressValidationService {
    void validatePostalCode(String postalCode);
    void validateStreet(String street);
    void validateCity(String city);
    void validateHouseNumber(String houseNumber);
    void validateFlatNumber(String flatNumber);

    void validateAddress(AddressDTO addressDTO);
}
