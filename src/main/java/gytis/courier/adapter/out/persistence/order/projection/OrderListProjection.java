package gytis.courier.adapter.out.persistence.order.projection;

import java.time.LocalDateTime;

public interface OrderListProjection {
    Long getId();
    Long getUserId();
    String getDeliveryMethodName();
    String getStatus();
    LocalDateTime getCreateDate();
}
