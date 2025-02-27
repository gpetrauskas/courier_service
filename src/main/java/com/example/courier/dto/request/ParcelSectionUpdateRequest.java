package com.example.courier.dto.request;

public record ParcelSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status,
        String contents
) implements BaseOrderUpdateRequest {
}
