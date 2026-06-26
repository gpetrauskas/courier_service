package gytis.courier.application.port.in.personnotification;


public interface PersonNotificationCommandUseCase {
    void markAsRead(Long id, Long personId);
    void markAllAsRead(Long personId);
    void delete(Long id, Long personId);
    void deleteAll(Long personId);
}
