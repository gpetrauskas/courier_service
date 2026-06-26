package gytis.courier.application.port.out.parcel;

import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.order.Parcel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ParcelCommandPort {
    int markAssigned(List<Long> parcelIds);
    int markUnassigned(List<Long> parcelIds);
    int updateStatus(Long parcelId, ParcelStatus parcelStatus);

    Optional<Parcel> find(Long id);
    Parcel update(Parcel parcel);
    void changeStatuses(Map<ParcelStatus, List<Long>> groupedIdsByStatuses);
}
