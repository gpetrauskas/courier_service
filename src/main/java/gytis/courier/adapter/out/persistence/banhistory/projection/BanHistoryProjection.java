package gytis.courier.adapter.out.persistence.banhistory.projection;

import java.time.LocalDateTime;

public interface BanHistoryProjection {
    Long getPersonId();
    boolean getBanned();
    String getActionBy();
    String getReason();
    LocalDateTime getActionTime();
}
