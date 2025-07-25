package com.example.courier.validation.shared;

import com.example.courier.dto.ApiResponseDTO;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class BaseValidator {
    protected void validateField(String value, String code, String message,
                                 Predicate<String> validator, List<ApiResponseDTO> errors) {
        Optional.ofNullable(value)
                .filter(v -> !v.isBlank())
                .filter(v -> !validator.test(v))
                .ifPresent(v -> errors.add(new ApiResponseDTO(code, message)));
    }
}
