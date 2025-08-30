package com.example.courier.dto.request.order;

import com.example.courier.service.order.handler.OrderUpdateHandler;
import com.example.courier.validation.shared.AtLeastOneField;

@AtLeastOneField
public record PaymentSectionUpdateRequest(
        Long id,
        String sectionToEdit,
        String status
) implements BaseOrderUpdateRequest {
    @Override
    public void applyUpdate(OrderUpdateHandler handler) {
        handler.handle(this);
    }
}
