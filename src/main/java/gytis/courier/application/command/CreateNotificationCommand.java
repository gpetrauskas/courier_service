package gytis.courier.application.command;

import gytis.courier.domain.notification.NotificationTarget;

public record CreateNotificationCommand(
        String title,
        String message,
        NotificationTarget target
) {
}
