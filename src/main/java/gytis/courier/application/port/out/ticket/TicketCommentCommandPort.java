package gytis.courier.application.port.out.ticket;

import gytis.courier.domain.ticket.TicketComment;

public interface TicketCommentCommandPort {
    void save(Long ticketId, TicketComment comment);
}
