package com.example.courier.dto.request.order;

import com.example.courier.validation.shared.AtLeastOneField;

@AtLeastOneField
public record PaymentSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status
) implements BaseOrderUpdateRequest {
}
