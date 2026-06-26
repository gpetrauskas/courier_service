package gytis.courier.adapter.out.strategy.notification;

import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BroadcastNotificationStrategy implements NotificationDeliveryStrategy {
    private final PersonQueryPort personQueryPort;
    private final PersonNotificationCommandPort personNotificationCommandPort;

    public BroadcastNotificationStrategy(PersonQueryPort personQueryPort, PersonNotificationCommandPort personNotificationCommandPort) {
        this.personQueryPort = personQueryPort;
        this.personNotificationCommandPort = personNotificationCommandPort;
    }

    @Override
    public Class<? extends NotificationTarget> getSupportedType() {
        return NotificationTarget.Broadcast.class;
    }

    @Override
    public void deliver(Notification notification) {
        if (!(notification.getTarget() instanceof NotificationTarget.Broadcast(NotificationTargetType type))) {
            throw new IllegalStateException("Wrong target type");
        }

        int page = 0;
        int batchSize = 1000;
        List<Long> batch;

        do {
            batch = switch (type) {
                case USER -> personQueryPort.getAllActiveUserIds(page, batchSize);
                case COURIER -> personQueryPort.getAllActiveCourierIds(page, batchSize);
                case ADMIN -> personQueryPort.getAllActiveAdminIds(page, batchSize);
            };

            if (!batch.isEmpty()) {
                personNotificationCommandPort.deliverToRecipients(notification.getId(), batch);
            }

            page++;
        } while (batch.size() == batchSize);
    }
}