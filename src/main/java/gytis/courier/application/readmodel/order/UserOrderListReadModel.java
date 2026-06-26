package gytis.courier.application.readmodel.order;

import java.time.LocalDateTime;

public record UserOrderListReadModel(
        Long id,
        String deliveryMethodName,
        String status,
        LocalDateTime createDate
) {
}
