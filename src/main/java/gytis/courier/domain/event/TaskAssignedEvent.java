package gytis.courier.domain.event;

public record TaskAssignedEvent(
        Long courierId
) implements DomainEvent {
}
