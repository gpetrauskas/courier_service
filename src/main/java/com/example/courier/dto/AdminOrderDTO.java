package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;

import java.time.LocalDateTime;

public record AdminOrderDTO(Long id, UserResponseDTO user, OrderAddress senderAddress, OrderAddress recipientAddress, Package packageDetails,
                            String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {
    public static AdminOrderDTO fromOrder(Order order) {
        UserResponseDTO user = UserResponseDTO.fromUser(order.getUser());
        return new AdminOrderDTO(order.getId(), user, order.getSenderAddress(), order.getRecipientAddress(),
                order.getPackageDetails(), order.getDeliveryPreferences(), order.getStatus(), order.getCreateDate());
    }
}
