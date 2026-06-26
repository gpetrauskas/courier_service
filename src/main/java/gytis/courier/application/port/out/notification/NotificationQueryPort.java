package gytis.courier.application.port.out.notification;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.AdminNotificationQuery;
import gytis.courier.application.readmodel.notification.NotificationReadModel;

public interface NotificationQueryPort {
    PageResult<NotificationReadModel> getAll(PageQuery pageQuery, AdminNotificationQuery query);
}
