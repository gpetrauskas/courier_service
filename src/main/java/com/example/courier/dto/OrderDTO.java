package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Package;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record OrderDTO(Long id, String senderAddress, String recipientAddress, Package packageDetails,
                       String deliveryPreferences, OrderStatus status, LocalDateTime createTime) {

    public String getCreateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.createTime.format(formatter);
    }
}
