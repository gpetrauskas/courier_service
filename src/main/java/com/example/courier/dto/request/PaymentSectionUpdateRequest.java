package com.example.courier.dto.request;

public record PaymentSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status
) implements BaseOrderUpdateRequest {
}
