package gytis.courier.adapter.out.persistence.refreshtoken;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jti;
    private Long personId;
    private boolean used;
    private boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

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

    public void setId(Long id) {
        this.id = id;
    }
    public void setJti(String jti) {
        this.jti = jti;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
