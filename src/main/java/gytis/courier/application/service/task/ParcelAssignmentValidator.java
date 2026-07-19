package gytis.courier.application.service.task;

import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.TaskItemCreationSnapshot;
import gytis.courier.domain.task.TaskType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParcelAssignmentValidator {
    public static final Set<ParcelStatus> ELIGIBLE_STATUSES_FOR_PICKUP = Set.of(ParcelStatus.PICKING_UP);
    public static final Set<ParcelStatus> ELIGIBLE_STATUSES_FOR_DELIVERY = Set.of(ParcelStatus.PICKED_UP);

    private ParcelAssignmentValidator() {}

    public static void validate(List<Long> requestedIds, List<TaskItemCreationSnapshot> snapshotList, TaskType taskType) {
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
            if (taskType == TaskType.PICKUP && !ELIGIBLE_STATUSES_FOR_PICKUP.contains(snapshot.status())) {
                throw new IllegalStateException("parcel not available for current task type");
            } else if (taskType == TaskType.DELIVERY && !ELIGIBLE_STATUSES_FOR_DELIVERY.contains(snapshot.status())) {
                throw new IllegalStateException("parcel not available for current task type");
            }
        }
    }
}
