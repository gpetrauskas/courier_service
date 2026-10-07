package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public record MyCourierInfoReadModel(
        String name,
        String email,
        LocalDateTime createdDate,
        boolean activeTask
) implements MyInfoReadModel {
}
