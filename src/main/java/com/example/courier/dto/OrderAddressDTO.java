package com.example.courier.dto;

import com.example.courier.domain.OrderAddress;

public record OrderAddressDTO(Long id, String city, String street, String houseNumber, String flatNumber, String phoneUmber,
                              String postCode, String name) {
    public static OrderAddressDTO fromOrderAddress(OrderAddress orderAddress) {
        return new OrderAddressDTO(
                orderAddress.getId(), orderAddress.getDetails().city(),
                orderAddress.getDetails().street(), orderAddress.getDetails().houseNumber(),
                orderAddress.getDetails().flatNumber(), orderAddress.getDetails().phoneNumber(),
                orderAddress.getDetails().postCode(), orderAddress.getDetails().name()
        );
    }
}
