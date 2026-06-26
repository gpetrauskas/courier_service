package gytis.courier.adapter.out.persistence.notification.projection;

import java.time.LocalDateTime;

public interface PersonNotificationProjection {
    Long getNotificationId();
    String getTitle();
    String getMessage();
    Boolean getRead();
    LocalDateTime getReceivedAt();
}
