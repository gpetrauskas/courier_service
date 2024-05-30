package com.example.courier.dto;

import com.example.courier.domain.Address;

public record AddressDTO(String city, String street, String houseNumber, String flatNumber, String phoneNumber,
                         String postCode, String name) {

    public static AddressDTO fromAddress(Address address) {
        return new AddressDTO(
                address.getCity(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getFlatNumber(),
                address.getPhoneNumber(),
                address.getPostCode(),
                address.getName()
        );
    }

    public static Address toAddress(AddressDTO addressDTO) {
        Address address = new Address();
        address.setCity(addressDTO.city());
        address.setStreet(addressDTO.street());
        address.setHouseNumber(addressDTO.houseNumber());
        address.setFlatNumber(addressDTO.flatNumber());
        address.setPhoneNumber(addressDTO.phoneNumber());
        address.setPostCode(addressDTO.postCode());
        address.setName(addressDTO.name());

        return address;
    }
}
