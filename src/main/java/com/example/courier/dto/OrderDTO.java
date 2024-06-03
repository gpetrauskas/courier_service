package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record OrderDTO(Long id, AddressDTO senderAddress, AddressDTO recipientAddress, PackageDTO packageDetails,
                       String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {
}
