package gytis.courier.adapter.out.persistence.activitylog;

import gytis.courier.domain.activitylog.ActivityCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ActivityLogJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String role;
    private String action;
    private String description;
    @Enumerated(EnumType.STRING)
    private ActivityCategory category;
    private LocalDateTime createdAt;

    public ActivityLogJpaEntity() {}

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ActivityCategory getCategory() { return category; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public void setCategory(ActivityCategory category) { this.category = category; }
}
