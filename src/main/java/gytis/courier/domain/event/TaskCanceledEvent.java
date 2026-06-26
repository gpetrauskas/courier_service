package gytis.courier.domain.event;

public record TaskCanceledEvent(
        Long taskId,
        Long adminId
) implements DomainEvent {
}
