package gytis.courier.domain.event;

import gytis.courier.domain.task.ParcelStatusUpdate;

import java.time.LocalDateTime;
import java.util.List;

public record TaskCompletedEvent(
        Long taskId,
        Long courierId,
        LocalDateTime completedAt,
        List<Long> failed,
        List<ParcelStatusUpdate> success
) implements DomainEvent {
}
