package com.example.courier.dto;

import java.time.LocalDateTime;

public record OrderDTO(Long id, String senderAddress, String recipientAddress, PackageDTO packageDetails,
                       String deliveryPreferences, String status, LocalDateTime createTime) {
}
