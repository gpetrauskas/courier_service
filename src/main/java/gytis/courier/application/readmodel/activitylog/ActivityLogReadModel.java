package gytis.courier.application.readmodel.activitylog;

import gytis.courier.domain.activitylog.ActivityCategory;

import java.time.LocalDateTime;

public record ActivityLogReadModel(
        Long id,
        String role,
        String userEmail,
        ActivityCategory category,
        String action,
        String description,
        LocalDateTime createdAt
) {
}
