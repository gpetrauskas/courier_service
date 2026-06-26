package gytis.courier.application.query.filter;

import gytis.courier.domain.notification.NotificationTargetType;

public record AdminNotificationQuery(
        String keyword,
        NotificationTargetType type
) {
}
