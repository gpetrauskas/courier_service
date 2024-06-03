package com.example.courier.dto;

import com.example.courier.domain.Address;

public record AddressDTO(String city, String street, String houseNumber, String flatNumber, String phoneNumber,
                         String postCode, String name) {


}
