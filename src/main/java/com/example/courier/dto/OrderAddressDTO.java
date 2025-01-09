package com.example.courier.dto;

import com.example.courier.domain.OrderAddress;

public record OrderAddressDTO(Long id, String city, String street, String houseNumber, String flatNumber, String phoneUmber,
                              String postCode, String name) {
    public static OrderAddressDTO fromOrderAddress(OrderAddress orderAddress) {
        return new OrderAddressDTO(
                orderAddress.getId(), orderAddress.getCity(),
                orderAddress.getStreet(), orderAddress.getHouseNumber(),
                orderAddress.getFlatNumber(), orderAddress.getPhoneNumber(),
                orderAddress.getPostCode(), orderAddress.getName()
        );
    }
}
