package gytis.courier.application.command;

import gytis.courier.domain.ticket.TicketPriority;
import gytis.courier.domain.ticket.TicketStatus;

import java.util.Optional;

public record UpdateTicketCommand(
        Optional<TicketPriority> priority,
        Optional<TicketStatus> status
) {
}
