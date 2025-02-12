package com.example.courier.dto;

public record CourierTaskItemDTO(Long id, String weight, String dimensions, String contents,
                                 String deliveryPreference, OrderAddressDTO customerAddress) {
}
