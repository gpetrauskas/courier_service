package gytis.courier.application.service.notification;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.notification.NotificationQueryUseCase;
import gytis.courier.application.port.out.notification.NotificationQueryPort;
import gytis.courier.application.query.filter.AdminNotificationQuery;
import gytis.courier.application.readmodel.notification.NotificationReadModel;
import org.springframework.stereotype.Service;

@Service
public class NotificationQueryService implements NotificationQueryUseCase {
    private final NotificationQueryPort port;

    public NotificationQueryService(NotificationQueryPort port) {
        this.port = port;
    }

    @Override
    public PageResult<NotificationReadModel> getAll(PageQuery pageQuery, AdminNotificationQuery query) {
        return port.getAll(pageQuery, query);
    }
}
