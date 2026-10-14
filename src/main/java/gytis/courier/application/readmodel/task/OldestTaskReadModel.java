package gytis.courier.application.readmodel.task;

import java.time.LocalDateTime;

public record OldestTaskReadModel(
        long id,
        LocalDateTime createdAt
) {
}
