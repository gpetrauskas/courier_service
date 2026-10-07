package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface CourierInfoProjection {
    String getName();
    String getEmail();
    LocalDateTime getCreatedDate();
    boolean hasActiveTask();
}
