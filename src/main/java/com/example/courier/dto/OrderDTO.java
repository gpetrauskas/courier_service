package com.example.courier.dto;

import com.example.courier.common.OrderStatus;

import java.time.LocalDateTime;

public record OrderDTO(Long id, AddressDTO senderAddress, AddressDTO recipientAddress, ParcelDTO parcelDetails,
                       String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {
}
