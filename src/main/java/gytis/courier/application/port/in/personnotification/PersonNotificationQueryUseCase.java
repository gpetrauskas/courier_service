package gytis.courier.application.port.in.personnotification;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PersonNotificationPageResult;

public interface PersonNotificationQueryUseCase {
    PersonNotificationPageResult getAll(PageQuery pageQuery, Long personId);
    PersonNotificationPageResult getPageContainingNotification(Long id, Long personId, int pageSize);
}
