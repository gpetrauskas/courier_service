package com.example.courier.dto;

import com.example.courier.domain.Package;

import java.time.LocalDateTime;

public record OrderDTO(Long id, String senderAddress, String recipientAddress, Package packageDetails,
                       String deliveryPreferences, String status, LocalDateTime createTime) {
}
