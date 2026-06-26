package gytis.courier.application.port.out.parcel;

import gytis.courier.domain.order.ParcelStatus;

import java.util.List;
import java.util.Optional;

public interface ParcelQueryPort {
    Optional<ParcelStatus> findStatusByTrackingNumber(String trackingNumber);
    Long getCountByStatusAndNotAssigned(ParcelStatus status);
    Long test(List<ParcelStatus> statuses);
}
