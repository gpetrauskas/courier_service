package gytis.courier.application.service.parcel;

import gytis.courier.application.port.in.parcel.ParcelQueryUseCase;
import gytis.courier.application.port.out.parcel.ParcelQueryPort;
import gytis.courier.application.readmodel.parcel.AvailableParcelsCountReadModel;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParcelQueryService implements ParcelQueryUseCase {
    private final static List<ParcelStatus> pickup = List.of(ParcelStatus.PICKING_UP);
    private final static List<ParcelStatus> delivery = List.of(ParcelStatus.PICKED_UP);
    private final static List<ParcelStatus> failed = List.of(ParcelStatus.FAILED_PICKUP, ParcelStatus.FAILED_DELIVERY);
    private final ParcelQueryPort port;

    public ParcelQueryService(ParcelQueryPort port) {
        this.port = port;
    }

    @Override
    public AvailableParcelsCountReadModel availableParcelsCount() {

        System.out.println("alio");
        Long pickingCount = getAvailableParcelsCountByStatus(ParcelStatus.PICKING_UP);
        Long deliveringCount = getAvailableParcelsCountByStatus(ParcelStatus.PICKED_UP);

        System.out.println(pickingCount + " and " + deliveringCount);
        return new AvailableParcelsCountReadModel(pickingCount, deliveringCount);
/*
        Long pickupCount = test(pickup);
        Long deliveringCount = test(delivery);
        Long failedCount = test(failed);

        return new AvailableParcelsCountReadModel(pickupCount, deliveringCount, failedCount);*/
    }

    @Override
    public ParcelStatus track(String trackingNumber) {
        return port.findStatusByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel was not found"));
    }

    //helper
    private Long getAvailableParcelsCountByStatus(ParcelStatus status) {
        return port.getCountByStatusAndNotAssigned(status);
    }

    private Long test(List<ParcelStatus> statuses) {
        return port.test(statuses);
    }
}
