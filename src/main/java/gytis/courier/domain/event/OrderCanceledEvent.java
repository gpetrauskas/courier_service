package gytis.courier.domain.event;

public record OrderCanceledEvent(Long orderId) implements DomainEvent {
}
