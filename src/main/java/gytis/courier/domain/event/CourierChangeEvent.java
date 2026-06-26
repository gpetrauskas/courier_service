package gytis.courier.domain.event;

public record CourierChangeEvent(Long taskId, Long oldCourier, Long newCourier) implements DomainEvent{
}
