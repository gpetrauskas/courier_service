package com.example.courier.service.validation;

import com.example.courier.common.AddressValidationMode;
import com.example.courier.dto.AddressDTO;

public interface AddressValidationService {
    void validateAddress(AddressDTO addressDTO, AddressValidationMode mode);
}
