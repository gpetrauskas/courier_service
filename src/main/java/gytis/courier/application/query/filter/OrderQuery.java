package gytis.courier.application.query.filter;

import gytis.courier.domain.order.OrderStatus;

public record OrderQuery(
        OrderStatus orderStatus,
        Long id
) {
}
