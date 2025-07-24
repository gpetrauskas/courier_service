package com.example.courier.service.validation;

import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;

import java.util.List;

public interface AddressValidationService {
    void validatePostalCode(String postalCode);
    void validateStreet(String street);
    void validateCity(String city);
    void validateHouseNumber(String houseNumber);
    void validateFlatNumber(String flatNumber);

    List<ApiResponseDTO> validateAddress(AddressDTO addressDTO);
}
