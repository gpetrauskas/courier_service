package com.example.courier.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record PersonNotificationId(
        @Column(name = "person_id", nullable = false) Long personId,
        @Column(name = "notification_id", nullable = false) Long notificationId
) implements Serializable {
}
