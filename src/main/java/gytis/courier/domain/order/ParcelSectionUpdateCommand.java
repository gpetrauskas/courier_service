package gytis.courier.domain.order;

public record ParcelSectionUpdateCommand(
        ParcelStatus status,
        String contents
) {
}
