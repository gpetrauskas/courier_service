package com.example.courier.validation.adminorderupdate;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Order;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.service.deliveryoption.DeliveryOptionService;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateValidator {
    private final DeliveryOptionService deliveryOptionService;

    public OrderUpdateValidator(DeliveryOptionService deliveryOptionService) {
        this.deliveryOptionService = deliveryOptionService;
    }

    public void validateOrderSectionUpdate(OrderSectionUpdateRequest updateRequest, Order existingOrder) {
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
