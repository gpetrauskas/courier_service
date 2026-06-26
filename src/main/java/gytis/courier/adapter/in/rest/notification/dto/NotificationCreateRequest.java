package gytis.courier.adapter.in.rest.notification.dto;

public record NotificationCreateRequest(
        String title,
        String message,
        NotificationTargetRequest target
) {
}
