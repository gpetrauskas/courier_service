package gytis.courier.application.port.in.parcel;

import gytis.courier.application.readmodel.parcel.AvailableParcelsCountReadModel;
import gytis.courier.domain.order.ParcelStatus;

public interface ParcelQueryUseCase {
    AvailableParcelsCountReadModel availableParcelsCount();
    ParcelStatus track(String trackingNumber);
}
