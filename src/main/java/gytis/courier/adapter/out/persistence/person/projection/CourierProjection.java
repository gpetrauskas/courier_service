package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface CourierProjection {
    Long getId();
    String getName();
    String getEmail();
    String getPhoneNumber();
    boolean isBlocked();
    boolean isDeleted();
    LocalDateTime getDeletedDate();
}
