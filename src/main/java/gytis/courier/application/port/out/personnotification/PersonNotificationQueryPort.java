package gytis.courier.application.port.out.personnotification;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PersonNotificationPageResult;
import gytis.courier.domain.personnotification.PersonNotification;

import java.util.Optional;

public interface PersonNotificationQueryPort {
    PersonNotification findByPersonIdAndNotificationId(Long personId, Long notificationId);
    PersonNotificationPageResult getAll(Long personId, PageQuery query);
    Optional<Integer> findIndex(Long myId, Long notificationId);
}
