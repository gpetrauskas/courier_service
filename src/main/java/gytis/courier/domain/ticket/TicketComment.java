package gytis.courier.domain.ticket;

import java.time.LocalDateTime;
import java.util.Objects;

public class TicketComment {
    private Long id;
    private Long authorId;
    private String authorName;
    private String message;
    private LocalDateTime createdAt;

    protected TicketComment() {}

    public static TicketComment restore(Long id, Long userId, String authorName, String message, LocalDateTime createdAt) {
        TicketComment comment = new TicketComment();
        comment.id = id;
        comment.authorId = userId;
        comment.authorName = authorName;
        comment.message = message;
        comment.createdAt = createdAt;

        return comment;
    }

    public static TicketComment create(Long authorId, String authorName, String message) {
        TicketComment comment = new TicketComment();
        comment.authorId = Objects.requireNonNull(authorId);
        comment.authorName = Objects.requireNonNull(authorName);
        comment.message = Objects.requireNonNull(message);
        comment.createdAt = LocalDateTime.now();

        return comment;
    }

    public Long getId() { return id; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
