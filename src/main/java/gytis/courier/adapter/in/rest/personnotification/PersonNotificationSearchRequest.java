package gytis.courier.adapter.in.rest.personnotification;

public record PersonNotificationSearchRequest(
        int page,
        int size,
        String sortField,
        String direction
) {
}
