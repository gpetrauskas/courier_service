package gytis.courier.application.port.out.notification;

import gytis.courier.domain.notification.Notification;

public interface NotificationCommandPort {
    void save(Notification notification);
    Notification create(Notification notification);
    void delete(Long id);
}
