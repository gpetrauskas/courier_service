package gytis.courier.application.readmodel.order;

import gytis.courier.domain.order.OrderStatus;
import gytis.courier.domain.order.ParcelStatus;

import java.time.LocalDateTime;

public record OrderUserDetailReadModel(
        Long id,
        String deliveryMethodName,
        OrderStatus status,
        LocalDateTime createDate,

        String senderAddress,
        String recipientAddress,
        String contents,
        String weightName,
        String dimensionsName,
        String trackingNumber,
        ParcelStatus parcelStatus
) {
}
