package com.example.courier.dto;

import com.example.courier.common.ParcelStatus;

public record DeliveryTaskItemDTO(
        Long id, ParcelDTO parcelDTO, OrderAddressDTO senderAddress,
        OrderAddressDTO recipientAddress, ParcelStatus status) {
}
