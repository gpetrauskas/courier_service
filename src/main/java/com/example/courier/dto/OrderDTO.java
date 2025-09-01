package com.example.courier.dto;

import com.example.courier.common.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrderDTO(
        Long id,
        @NotNull AddressDTO senderAddress,
        @NotNull AddressDTO recipientAddress,
        @NotNull ParcelDTO parcelDetails,
        @NotNull String preference,
        OrderStatus status,
        LocalDateTime createTime
) {
}
