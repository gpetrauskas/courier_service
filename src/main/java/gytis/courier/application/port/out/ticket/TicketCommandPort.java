package gytis.courier.application.port.out.ticket;

import gytis.courier.domain.ticket.Ticket;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TicketCommandPort {
    Optional<Ticket> findById(Long ticketId);

    void save(Ticket ticket);
    Ticket create(Ticket ticket);

    void updateTimestamp(Long ticketId, LocalDateTime timestamp);
}
