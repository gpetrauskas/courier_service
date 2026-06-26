package gytis.courier.domain.event;

public record CourierCheckedInEvent(
        Long taskId,
        Long courierId
) implements DomainEvent {
}
