package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record AdminCourierDetailsReadModel(
        Long id,
        String name,
        String email,
        String role,
        String phoneNumber,
        boolean blocked,
        boolean deleted,
        LocalDateTime deletedDate,
        boolean hasActiveTask,
        int completedDeliveries
) implements AdminPersonDetailsReadModel {
}
