package gytis.courier.application.command;

import gytis.courier.domain.ticket.TicketPriority;

public record CreateTicketCommand(
        String title,
        String description,
        TicketPriority ticketPriority,
        Long personId
) {
}
