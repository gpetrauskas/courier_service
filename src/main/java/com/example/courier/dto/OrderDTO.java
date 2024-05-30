package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record OrderDTO(Long id, AddressDTO senderAddress, AddressDTO recipientAddress, PackageDTO packageDetails,
                       String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {

    public static OrderDTO fromOrder(Order order) {
        return new OrderDTO(
                order.getId(), AddressDTO.fromAddress(order.getSenderAddress()),
                AddressDTO.fromAddress(order.getRecipientAddress()),
                PackageDTO.packageToDTO(order.getPackageDetails()), order.getDeliveryPreferences(),
                order.getStatus(), order.getCreateDate()
        );
    }

    public String getCreateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.createTime.format(formatter);
    }
}
