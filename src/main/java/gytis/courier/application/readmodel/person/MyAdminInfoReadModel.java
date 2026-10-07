package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record MyAdminInfoReadModel(
        String name,
        String email,
        LocalDateTime createdDate,
        int createdTasks
) implements MyInfoReadModel {
}
