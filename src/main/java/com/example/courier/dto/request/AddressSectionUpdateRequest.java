package com.example.courier.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressSectionUpdateRequest(
        @NotNull Long id,
        @NotBlank String sectionToEdit,
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) implements BaseOrderUpdateRequest {
}
