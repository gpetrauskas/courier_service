package com.example.courier.dto.request;

public record OrderSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status,
        String deliveryPreferences
) implements BaseOrderUpdateRequest {
}
