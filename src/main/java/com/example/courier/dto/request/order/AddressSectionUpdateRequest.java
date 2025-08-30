package com.example.courier.dto.request.order;

import com.example.courier.service.order.handler.OrderUpdateHandler;
import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@AtLeastOneField
public record AddressSectionUpdateRequest(
        @NotNull Long id,
        @NotBlank String sectionToEdit,
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) implements BaseOrderUpdateRequest {
    @Override
    public void applyUpdate(OrderUpdateHandler handler) {
        handler.handle(this);
    }
}
