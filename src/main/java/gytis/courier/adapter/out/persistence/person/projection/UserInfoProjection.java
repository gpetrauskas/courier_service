package gytis.courier.adapter.out.persistence.person.projection;

import java.time.LocalDateTime;

public interface UserInfoProjection {
    String getName();
    String getEmail();
    int getOrderCount();
    boolean getSubscribed();
    String getDefaultAddress();
    String getPhoneNumber();
    LocalDateTime getCreatedDate();
}
