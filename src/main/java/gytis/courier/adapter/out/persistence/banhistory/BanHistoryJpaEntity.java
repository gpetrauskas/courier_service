package gytis.courier.adapter.out.persistence.banhistory;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ban_history")
public class BanHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long personId;

    @Column(nullable = false)
    private boolean banned;

    @Column(nullable = false)
    private String actionBy;

    @Column
    private String reason;

    @Column(nullable = false)
    private LocalDateTime actionTime;

    protected BanHistoryJpaEntity() {}

    private BanHistoryJpaEntity(Long personId, boolean banned, String actionBy, String reason) {
        this.personId = Objects.requireNonNull(personId);
        this.banned = banned;
        this.actionBy = Objects.requireNonNull(actionBy);
        this.reason = reason;
        this.actionTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getPersonId() { return personId; }
    public boolean isBanned() { return banned; }
    public String getActionBy() { return actionBy; }
    public String getReason() { return reason; }
    public LocalDateTime getActionTime() { return actionTime; }

    public void setPersonId(Long personId) { this.personId = personId; }
    public void setIsBanned(boolean banned) { this.banned = banned; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }
    public void setReason(String reason) { this.reason = reason; }
    public void setActionTime(LocalDateTime actionTime) { this.actionTime = actionTime; }
}
