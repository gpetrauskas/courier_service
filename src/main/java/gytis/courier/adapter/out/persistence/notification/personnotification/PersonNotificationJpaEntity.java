package gytis.courier.adapter.out.persistence.notification.personnotification;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_notifications")
public class PersonNotificationJpaEntity {
    @EmbeddedId
    private PersonNotificationJpaId id;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    protected PersonNotificationJpaEntity() {}

    public PersonNotificationJpaEntity(Long personid, Long notificationId) {
        this.id = new PersonNotificationJpaId(personid, notificationId);
    }

    public PersonNotificationJpaId getId() { return id; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; }

    public void setRead(boolean read) { isRead = read; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    @PrePersist
    protected void onCreate() {
        this.receivedAt = LocalDateTime.now();
    }
}
