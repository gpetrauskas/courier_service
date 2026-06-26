package gytis.courier.domain.event;

public record CourierReturningEvent(
        Long taskId,
        Long courierId
) implements DomainEvent {
}
