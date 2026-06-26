package gytis.courier.domain.personnotification;


import java.time.LocalDateTime;

public class PersonNotification {
    private PersonNotificationId id;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime receivedAt;

    private PersonNotification() {}

    public PersonNotification(PersonNotificationId id) {
        this.id = id;
    }

    public PersonNotificationId getId() { return id; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; }

    public void markAsRead() {
        if (!isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }
}
