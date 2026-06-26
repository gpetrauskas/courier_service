package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record AdminAdminDetailsReadModel(
        Long id,
        String name,
        String email,
        String role,
        String phoneNumber,
        boolean blocked,
        boolean deleted,
        LocalDateTime deletedDate,
        int createdTasks
) implements AdminPersonDetailsReadModel {
}
