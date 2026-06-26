package gytis.courier.adapter.out.persistence.order.projection;

import gytis.courier.domain.order.OrderStatus;

import java.time.LocalDateTime;

public interface AdminOrderListProjection {
    Long getId();
    Long getUserId();
    String getDeliveryMethodName();
    OrderStatus getStatus();
    LocalDateTime getCreateDate();
}
