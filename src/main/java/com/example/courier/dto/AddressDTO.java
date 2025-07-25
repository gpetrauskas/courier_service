package com.example.courier.dto;

public record AddressDTO(
        Long id,
        String city,
        String street,
        String houseNumber,
        String flatNumber,
        String phoneNumber,
        String postCode,
        String name
) {
}
