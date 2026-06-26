package gytis.courier.application.readmodel.order;

import gytis.courier.domain.order.ParcelStatus;

import java.time.LocalDateTime;

public record OrderForTaskReadModel(
        Long orderId,
        Long parcelId,
        ParcelStatus parcelStatus,
        String contents,
        int failuresCount,
        String deliveryMethodName,
        String weight,
        String dimensions,
        String fullAddress,
        String customerContacts,
        LocalDateTime createDate
) {
}
