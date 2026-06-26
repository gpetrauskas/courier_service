package gytis.courier.application.readmodel.ticket;

import java.time.LocalDateTime;

public record TicketCommentReadModel(
        String message,
        String authorName,
        LocalDateTime createdAt
) {
}
