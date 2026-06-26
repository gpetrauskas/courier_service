package gytis.courier.application.service.parcel;

import gytis.courier.application.port.in.parcel.ParcelCommandUseCase;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.parcel.ParcelCommandPort;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.order.Parcel;
import gytis.courier.domain.task.ParcelStatusUpdate;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParcelCommandService implements ParcelCommandUseCase {
    private final ParcelCommandPort port;
    private final DomainEventPublisher eventPublisher;

    public ParcelCommandService(ParcelCommandPort port, DomainEventPublisher eventPublisher) {
        this.port = port;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    @Override
    public void handleTaskCompleted(List<ParcelStatusUpdate> successes, List<Long> failures) {
        List<Long> parcelIds = new ArrayList<>();
        successes.forEach(p -> parcelIds.add(p.parcelId()));
        parcelIds.addAll(failures);

        port.markUnassigned(parcelIds);

        List<Parcel> parcels = failures.stream()
                .map(this::incrementFailuresCount)
                .toList();

        parcels.forEach(p ->  {
            eventPublisher.publish(p.pullEvents());
            System.out.println("cia handle task completed kiek event: " + p.getEventSize());

        });
        port.changeStatuses(groupParcels(successes));
    }

    private Map<ParcelStatus, List<Long>> groupParcels(List<ParcelStatusUpdate> parcelStatusUpdates) {
        return parcelStatusUpdates.stream()
                .collect(Collectors.groupingBy(ParcelStatusUpdate::parcelStatus,
                        Collectors.mapping(ParcelStatusUpdate::parcelId, Collectors.toList())));
    }


    private Parcel incrementFailuresCount(Long parcelId) {
        Parcel parcel = port.find(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        parcel.failedDeliveryAttemptAdd();
        System.out.println("coa increment failures kiek event: " + parcel.getEventSize());
        return port.update(parcel);
    }
}
