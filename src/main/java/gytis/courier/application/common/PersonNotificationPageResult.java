package gytis.courier.application.common;

import gytis.courier.application.readmodel.personnotification.PersonNotificationReadModel;

public record PersonNotificationPageResult(
        PageResult<PersonNotificationReadModel> page,
        long unreadCount
) {
}
