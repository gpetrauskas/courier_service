package gytis.courier.application.readmodel.ticket;

import java.time.LocalDateTime;

public record CreatorSummaryReadModel(
        Long id,
        String name,
        String email,
        boolean blocked,
        boolean deleted,
        LocalDateTime deletedDate
) {
}
