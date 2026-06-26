package gytis.courier.domain.event;

public record PaymentConfirmedEvent(Long orderId) implements DomainEvent {
}
