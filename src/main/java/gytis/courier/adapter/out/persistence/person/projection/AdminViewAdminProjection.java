package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface AdminViewAdminProjection {
    Long getId();
    String getName();
    String getEmail();
    String getRole();
    String getPhoneNumber();
    boolean isBlocked();
    boolean isDeleted();
    LocalDateTime getDeletedDate();
    int getCreatedTasks();
}
