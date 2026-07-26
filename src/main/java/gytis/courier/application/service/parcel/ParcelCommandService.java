package gytis.courier.application.service.parcel;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
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
    private final ActivityLogUseCase logUseCase;

    public ParcelCommandService(ParcelCommandPort port, DomainEventPublisher eventPublisher, ActivityLogUseCase logUseCase) {
        this.port = port;
        this.eventPublisher = eventPublisher;
        this.logUseCase = logUseCase;
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
        });

        port.changeStatuses(groupParcels(successes));

        logUseCase.saveLog("SYSTEM", "task completed", parcelIds.size() + " parcels was unassigned. Failed parcels: " + failures.size());
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

        logUseCase.saveLog("SYSTEM", "task completed", "Parcel#" + parcelId + " pickup/delivery failed " + parcel.getFailuresCount() + " times");
        return port.update(parcel);
    }
}
