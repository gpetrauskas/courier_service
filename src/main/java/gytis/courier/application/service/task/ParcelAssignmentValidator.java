package gytis.courier.application.service.task;

import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskItemCreationSnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParcelAssignmentValidator {
    public static final Set<ParcelStatus> ELIGIBLE_STATUSES = Set.of(
            ParcelStatus.DELIVERING,
            ParcelStatus.PICKING_UP,
            ParcelStatus.PICKED_UP,
            ParcelStatus.FAILED_PICKUP,
            ParcelStatus.FAILED_DELIVERY
    );

    private ParcelAssignmentValidator() {}

    public static void validate(List<Long> requestedIds, List<TaskItemCreationSnapshot> snapshotList) {
        Set<Long> requestedSet = new HashSet<>(requestedIds);
        Set<Long> fetchedSet = snapshotList.stream()
                .map(TaskItemCreationSnapshot::parcelId)
                .collect(Collectors.toSet());

        if (requestedSet.size() != requestedIds.size()) {
            throw new IllegalStateException("Duplicate parcel IDS in request");
        }

        if (!fetchedSet.equals(requestedSet)) {
            Set<Long> missing = new HashSet<>(requestedIds);
            missing.removeAll(fetchedSet);
            throw new IllegalStateException("Missing parcels: " + missing);
        }

        for (TaskItemCreationSnapshot snapshot : snapshotList) {
            for (int i = 0; i < snapshotList.size(); i++) {
                System.out.println(snapshot.status());
            }

            if (!ELIGIBLE_STATUSES.contains(snapshot.status())) {
                throw new IllegalStateException("Check parcels: invalid statuses");
            }
        }
    }
}
