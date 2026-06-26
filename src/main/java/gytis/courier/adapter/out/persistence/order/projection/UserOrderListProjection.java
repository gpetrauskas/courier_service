package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.order.OrderStatus;

import java.time.LocalDateTime;

public interface UserOrderListProjection {
    Long getId();
    String getDeliveryMethodName();
    OrderStatus getStatus();
    LocalDateTime getCreateDate();
}
