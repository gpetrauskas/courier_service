package gytis.courier.domain.event;

public record ParcelMaxFailuresReachedEvent(
        Long parcelId,
        int failuresCount
) implements DomainEvent {

}
