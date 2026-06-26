package gytis.courier.application.port.out.notification;

import gytis.courier.domain.notification.Notification;

public interface NotificationDeliveryPort {
    void deliver(Notification notification);
}
