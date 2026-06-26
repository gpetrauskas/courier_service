package gytis.courier.application.readmodel.notification;

import java.time.LocalDateTime;

public record NotificationReadModel(
        Long id,
        String title,
        String message,
        LocalDateTime createdAt
) {
}
