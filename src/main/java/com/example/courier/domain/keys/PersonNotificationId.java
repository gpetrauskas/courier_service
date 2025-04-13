package com.example.courier.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record PersonNotificationId(
        @Column(name = "person_id") Long personId,
        @Column(name = "notification_id") Long notificationId
) implements Serializable {
    public PersonNotificationId {
        if (personId == null) personId = 0L;
        if (notificationId == null) notificationId = 0L;
    }
}
