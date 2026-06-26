package gytis.courier.domain.event;

public record OrderAddressUpdatedEvent(
        Long parcelId,
        String selectedAddress
) implements DomainEvent {
}
