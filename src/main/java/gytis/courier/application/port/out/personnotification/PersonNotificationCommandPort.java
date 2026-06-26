package gytis.courier.application.port.out.personnotification;

import gytis.courier.domain.personnotification.PersonNotification;

import java.util.List;

public interface PersonNotificationCommandPort {
    void deliverToRecipients(Long notificationId, List<Long> ids);
    void markAsRead(Long id, Long personId);
    void markAllAsRead(Long personId);
    void save(PersonNotification notification);
    void ownDelete(Long myId, Long notificationId);
    void deleteAll(Long myId);
    int deleteAsAdmin(Long id);
}
