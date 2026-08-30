package gytis.courier.application.port.out.ticket;

import gytis.courier.domain.ticket.TicketComment;

public interface TicketCommentBroadcastPort {
    void broadcast(Long ticketId, TicketComment comment);
}
