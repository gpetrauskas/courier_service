package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Address;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import com.example.courier.domain.User;

import java.time.LocalDateTime;

public record AdminOrderDTO(Long id, UserResponseDTO user, Address senderAddress, Address recipientAddress, Package packageDetails,
                            String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {
    public static AdminOrderDTO fromOrder(Order order) {
        UserResponseDTO user = UserResponseDTO.fromUser(order.getUser());
        return new AdminOrderDTO(order.getId(), user, order.getSenderAddress(), order.getRecipientAddress(),
                order.getPackageDetails(), order.getDeliveryPreferences(), order.getStatus(), order.getCreateDate());
    }
}
