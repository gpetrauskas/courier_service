package gytis.courier.adapter.in.rest.notification.dto;

import gytis.courier.domain.notification.NotificationTargetType;

public record AdminNotificationQueryRequest(
        String keyword,
        NotificationTargetType type
) {
}
