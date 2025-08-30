package com.example.courier.dto.request.order;

import com.example.courier.service.order.handler.OrderUpdateHandler;
import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@AtLeastOneField
public record ParcelSectionUpdateRequest(
        @NotNull Long id,
        @NotBlank String sectionToEdit,
        String status,
        String contents
) implements BaseOrderUpdateRequest {
    @Override
    public void applyUpdate(OrderUpdateHandler handler) {
        handler.handle(this);
    }
}
