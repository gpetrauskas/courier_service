package com.example.courier.dto;

public record CourierParcelDTO(Long id, String weight, String dimensions, String contents,
                               String senderAddress, String senderName, String recipientName,
                               String recipientAddress, String recipientPhone,
                               String deliveryPreferences) {
}
