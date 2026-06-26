package gytis.courier.domain.banhistory;

import java.time.LocalDateTime;

public class BanHistory {
    private final Long personId;
    private final boolean banned;
    private final String actionBy;
    private final String reason;
    private final LocalDateTime actionTime;

    public BanHistory(Long personId, boolean banned, String actionBy, String reason) {
        this.personId = personId;
        this.banned = banned;
        this.actionBy = actionBy;
        this.reason = reason;
        this.actionTime = LocalDateTime.now();
    }

    public static BanHistory of(Long personId, boolean banned, String actionBy, String reason) {
        return new BanHistory(personId, banned, actionBy, reason);
    }

    public Long getPersonId() { return personId; }
    public boolean isBanned() { return banned; }
    public String getActionBy() { return actionBy; }
    public String getReason() { return reason; }
    public LocalDateTime getActionTime() { return actionTime; }
}
