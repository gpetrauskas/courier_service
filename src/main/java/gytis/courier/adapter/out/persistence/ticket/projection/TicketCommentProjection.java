package gytis.courier.adapter.out.persistence.ticket.projection;

import java.time.LocalDateTime;

public interface TicketCommentProjection {
    String getMessage();
    LocalDateTime getCreatedAt();
    String getAuthorName();

}
