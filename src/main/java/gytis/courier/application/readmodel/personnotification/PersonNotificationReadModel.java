package gytis.courier.application.readmodel.personnotification;

import java.time.LocalDateTime;

public record PersonNotificationReadModel(
        Long id,
        String title,
        String message,
        LocalDateTime receivedAt,
        boolean isRead
) {
}
