package gytis.courier.adapter.in.rest.notification.dto;

import java.time.LocalDateTime;

public record NotificationPersonResponse(
        Long id,
        String title,
        String message,
        LocalDateTime receivedAt,
        boolean isRead
) {
}