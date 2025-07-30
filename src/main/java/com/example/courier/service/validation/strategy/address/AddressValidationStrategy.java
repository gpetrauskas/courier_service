package com.example.courier.service.validation.strategy.address;

import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;

import java.util.List;

public interface AddressValidationStrategy {
    void validate(AddressDTO addressDTO, List<ApiResponseDTO> errors);
}
