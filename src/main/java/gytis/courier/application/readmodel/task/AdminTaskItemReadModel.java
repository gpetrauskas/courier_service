package gytis.courier.application.readmodel.task;

import gytis.courier.domain.order.ParcelStatus;

public record AdminTaskItemReadModel(
        Long id,
        Long parcelId,
        ParcelStatus parcelStatus,
        String deliveryMethodName,
        String contents,
        String weight,
        String dimensions,
        String address
) {
}
