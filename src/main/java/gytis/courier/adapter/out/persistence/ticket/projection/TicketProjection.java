package gytis.courier.adapter.out.persistence.ticket.projection;

import gytis.courier.adapter.out.persistence.person.projection.UserProjection;

import java.time.LocalDateTime;

public interface TicketProjection {
    Long getId();
    String getTitle();
    String getDescription();
    String getStatus();
    String getPriority();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getCompletedAt();

    UserProjection getCreatedBy();
}
