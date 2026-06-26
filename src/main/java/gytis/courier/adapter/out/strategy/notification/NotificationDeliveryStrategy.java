package gytis.courier.adapter.out.strategy.notification;

import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;

public interface NotificationDeliveryStrategy {
    Class<? extends NotificationTarget> getSupportedType();
    void deliver(Notification notification);
}
