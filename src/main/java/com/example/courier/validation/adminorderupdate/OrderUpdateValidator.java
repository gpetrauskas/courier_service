package com.example.courier.validation.adminorderupdate;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateValidator {
    private final DeliveryMethodService deliveryMethodService;

    public OrderUpdateValidator(DeliveryMethodService deliveryMethodService) {
        this.deliveryMethodService = deliveryMethodService;
    }

    public void validateOrderSectionStatusUpdate(OrderSectionUpdateRequest updateRequest, Order existingOrder) {
        validateOrderStatus(updateRequest.status(), existingOrder.getStatus().toString());
    }

    private void validateOrderStatus(String newStatus, String currentStatus) {
        if (newStatus != null && newStatus.equalsIgnoreCase(currentStatus)) {
            throw new IllegalArgumentException("Order status is already set to: " + newStatus);
        }

        if (newStatus != null) {
            OrderStatus.isValidStatus(newStatus);
        }
    }
}
