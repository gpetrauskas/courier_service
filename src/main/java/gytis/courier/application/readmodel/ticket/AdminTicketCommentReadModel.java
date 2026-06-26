package gytis.courier.application.readmodel.ticket;

import java.time.LocalDateTime;

public record AdminTicketCommentReadModel(
        Long id,
        Long authorId,
        String message,
        LocalDateTime createdAt,
        String authorName,
        String authorEmail
) {
}
