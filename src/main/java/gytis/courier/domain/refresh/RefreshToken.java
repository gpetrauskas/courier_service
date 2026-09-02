package gytis.courier.domain.refresh;

import java.time.LocalDateTime;
import java.util.Objects;

public class RefreshToken {
    private final Long id;
    private final String jti;
    private final Long personId;
    private boolean used;
    private boolean revoked;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public RefreshToken(Long id, String jti, Long personId, boolean used, boolean revoked, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.jti = jti;
        this.personId = personId;
        this.used = used;
        this.revoked = revoked;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(String jti, Long personId, LocalDateTime createdAt, LocalDateTime expiresAt) {
        Objects.requireNonNull(jti);
        Objects.requireNonNull(personId);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(expiresAt);

        return new RefreshToken(null, jti, personId, false, false, createdAt, expiresAt);
    }

    public Long getId() {
        return id;
    }
    public String getJti() {
        return jti;
    }
    public Long getPersonId() {
        return personId;
    }
    public boolean isUsed() {
        return used;
    }
    public boolean isRevoked() {
        return revoked;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
