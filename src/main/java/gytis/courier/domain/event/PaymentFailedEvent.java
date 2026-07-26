package gytis.courier.domain.event;

public record PaymentFailedEvent(Long orderId) implements DomainEvent, OutboxEvent {
}
