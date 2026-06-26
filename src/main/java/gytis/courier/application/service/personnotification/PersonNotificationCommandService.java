package gytis.courier.application.service.personnotification;

import gytis.courier.application.port.in.personnotification.PersonNotificationCommandUseCase;
import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import org.springframework.stereotype.Service;

@Service
public class PersonNotificationCommandService implements PersonNotificationCommandUseCase {
    private final PersonNotificationCommandPort port;

    public PersonNotificationCommandService(PersonNotificationCommandPort port) {
        this.port = port;
    }

    @Override
    public void markAsRead(Long id, Long personId) {
        port.markAsRead(id, personId);
    }

    @Override
    public void markAllAsRead(Long personId) {
        port.markAllAsRead(personId);
    }

    @Override
    public void delete(Long notificationId, Long personId) {
        port.ownDelete(personId, notificationId);
    }

    @Override
    public void deleteAll(Long personId) {
        port.deleteAll(personId);
    }
}
