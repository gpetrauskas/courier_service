package gytis.courier.application.readmodel.ticket;

import java.time.LocalDateTime;

public record TicketReadModel(
        Long id,
        String title,
        String description,
        String status,
        String priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {
}
