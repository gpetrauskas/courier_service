package com.example.courier.domain;

import com.example.courier.domain.keys.PersonNotificationId;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_notifications")
public class PersonNotification {

    @EmbeddedId
    private PersonNotificationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", insertable = false, updatable = false)
    private Notification notification;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "received_at", nullable = false)
    private final LocalDateTime receivedAt = LocalDateTime.now();

    protected PersonNotification() {}

    public PersonNotification(Person person, Notification notification) {
        this.person = person;
        this.notification = notification;
        this.id = new PersonNotificationId(person.getId(), notification.getId());
    }

    public Person getPerson() {
        return person;
    }

    public Notification getNotification() {
        return notification;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }
}
