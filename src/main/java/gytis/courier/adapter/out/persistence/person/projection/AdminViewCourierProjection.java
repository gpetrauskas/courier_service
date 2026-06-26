package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface AdminViewCourierProjection {
    Long getId();
    String getName();
    String getEmail();
    String getRole();
    String getPhoneNumber();
    boolean isBlocked();
    boolean isDeleted();
    LocalDateTime getDeletedDate();
    Boolean getActiveTask();
    int getCompletedDeliveries();
}
