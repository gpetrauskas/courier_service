package gytis.courier.application.readmodel.order;

import gytis.courier.domain.order.OrderStatus;

import java.time.LocalDateTime;

public record AdminOrderListReadModel(
        Long id,
        Long userId,
        String deliveryMethodName,
        OrderStatus status,
        LocalDateTime createDate
) {
}
