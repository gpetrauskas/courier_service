package gytis.courier.application.readmodel.parcel;

import gytis.courier.domain.order.ParcelStatus;

public record ParcelReadModel(
        Long id,
        String contents,
        String weight,
        String dimensions,
        String trackingNumber,
        ParcelStatus status
) {
}
