package com.example.courier.dto.request;

public record AddressSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) implements BaseOrderUpdateRequest {
}
