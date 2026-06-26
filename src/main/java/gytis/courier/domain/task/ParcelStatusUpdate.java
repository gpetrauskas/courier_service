package gytis.courier.domain.task;

import gytis.courier.domain.order.ParcelStatus;

public record ParcelStatusUpdate(
        Long parcelId,
        ParcelStatus parcelStatus
) {
}
