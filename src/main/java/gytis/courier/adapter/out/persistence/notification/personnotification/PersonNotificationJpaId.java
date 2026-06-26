package gytis.courier.adapter.out.persistence.notification.personnotification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PersonNotificationJpaId(
        @Column(name = "person_id", nullable = false) Long personId,
        @Column(name = "notification_id", nullable = false) Long notificationId
) {
}
