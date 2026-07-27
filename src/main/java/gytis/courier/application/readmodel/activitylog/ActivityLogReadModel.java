package gytis.courier.application.readmodel.activitylog;

import java.time.LocalDateTime;

public record ActivityLogReadModel(
        Long id, String role, String userEmail, String action, String description, LocalDateTime createdAt
) {
}
