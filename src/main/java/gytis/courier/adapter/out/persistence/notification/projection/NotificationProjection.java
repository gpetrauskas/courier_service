package gytis.courier.adapter.out.persistence.notification.projection;

import java.time.LocalDateTime;

public interface NotificationProjection {
    Long getId();
    String getTitle();
    String getMessage();
    LocalDateTime getCreatedAt();
}
