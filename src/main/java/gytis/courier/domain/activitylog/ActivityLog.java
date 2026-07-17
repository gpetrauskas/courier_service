package gytis.courier.domain.activitylog;

import java.time.LocalDateTime;

public class ActivityLog {
    private Long id;
    private final String email;
    private final String role;
    private final String action;
    private final String description;
    private final LocalDateTime createdAt;

    public ActivityLog(String email, String role, String action, String description) {
        this.email = email;
        this.role = role;
        this.action = action;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }


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
    public String getEmail() { return email; }
}
