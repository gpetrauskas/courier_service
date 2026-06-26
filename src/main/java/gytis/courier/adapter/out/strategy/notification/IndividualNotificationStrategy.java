package gytis.courier.adapter.out.strategy.notification;

import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IndividualNotificationStrategy implements NotificationDeliveryStrategy {
    private final PersonNotificationCommandPort personNotificationCommandPort;

    public IndividualNotificationStrategy(PersonNotificationCommandPort personNotificationCommandPort) {
        this.personNotificationCommandPort = personNotificationCommandPort;
    }

    @Override
    public Class<? extends NotificationTarget> getSupportedType() {
        return NotificationTarget.Individual.class;
    }

    @Override
    public void deliver(Notification notification) {
        if (!(notification.getTarget() instanceof NotificationTarget.Individual(Long personId))) {
            throw new IllegalArgumentException("Invalid target type for individual");
        }

        personNotificationCommandPort.deliverToRecipients(notification.getId(), List.of(personId));
    }
}
