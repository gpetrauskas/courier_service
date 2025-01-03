package com.example.courier.dto;

public record OrderAddressDTO(Long id, String city, String street, String houseNumber, String flatNumber, String phoneUmber,
                              String postCode, String name) {
}
