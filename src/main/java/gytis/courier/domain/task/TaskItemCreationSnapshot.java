package gytis.courier.domain.task;

import gytis.courier.domain.order.ParcelStatus;

public record TaskItemCreationSnapshot(
        Long parcelId,
        ParcelStatus status,
        Long senderAddressId,
        Long recipientAddressId,
        String contents,
        String deliveryMethodName
) {
}
