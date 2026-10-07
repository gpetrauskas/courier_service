package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface AdminInfoProjection {
    String getName();
    String getEmail();
    LocalDateTime getCreatedDate();
    int getCreatedTasks();
}
