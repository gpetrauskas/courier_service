package gytis.courier.application.service.notification;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.application.port.out.notification.NotificationDeliveryPort;
import gytis.courier.application.port.out.notification.NotificationCommandPort;
import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import gytis.courier.domain.notification.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCommandService implements NotificationCommandUseCase {
    private final NotificationCommandPort port;
    private final NotificationDeliveryPort deliveryPort;
    private final PersonNotificationCommandPort pnPort;

    public NotificationCommandService(NotificationCommandPort port, NotificationDeliveryPort deliveryPort, PersonNotificationCommandPort pnPort) {
        this.port = port;
        this.deliveryPort = deliveryPort;
        this.pnPort = pnPort;
    }

    @Override
    @Transactional
    public void create(CreateNotificationCommand command) {
        Notification notification = new Notification(command.title(), command.message(), command.target());
        Notification saved = port.create(notification);
        deliveryPort.deliver(saved);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        int deleted = pnPort.deleteAsAdmin(id);
        port.delete(id);

        return deleted;
    }
}
