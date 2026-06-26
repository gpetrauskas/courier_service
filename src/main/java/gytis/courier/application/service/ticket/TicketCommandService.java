package gytis.courier.application.service.ticket;

import gytis.courier.application.command.AddTicketCommentCommand;
import gytis.courier.application.command.CreateTicketCommand;
import gytis.courier.application.command.UpdateTicketCommand;
import gytis.courier.application.port.in.ticket.TicketCommandUseCase;
import gytis.courier.application.port.out.ticket.TicketCommandPort;
import gytis.courier.application.port.out.ticket.TicketCommentCommandPort;
import gytis.courier.domain.ticket.Ticket;
import gytis.courier.domain.ticket.TicketComment;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TicketCommandService implements TicketCommandUseCase {
    private final TicketCommandPort port;
    private final TicketCommentCommandPort commentPort;

    public TicketCommandService(TicketCommandPort port, TicketCommentCommandPort commentPort) {
        this.port = port;
        this.commentPort = commentPort;
    }

    @Override
    public void create(CreateTicketCommand command) {
        System.out.println(command.ticketPriority() + " " + command.description() + " " + command.personId() +
                " " + command.title());
        Ticket ticket = Ticket.create(command);
        port.create(ticket);
    }

    public void addComment(AddTicketCommentCommand command) {
        Ticket ticket = port.findById(command.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        boolean isAdmin = "ADMIN".equals(command.role());
        TicketComment comment = ticket.addComment(command.personId(), isAdmin, command.message());

        commentPort.save(ticket.getId(), comment);

        port.updateTimestamp(ticket.getId(), ticket.getUpdatedAt());
    }

    @Override
    public void update(Long ticketId, UpdateTicketCommand command) {
        Ticket ticket = port.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        command.status().ifPresent(ticket::changeStatus);
        command.priority().ifPresent(ticket::changePriority);

        port.save(ticket);
    }

    @Override
    public void close(Long ticketId, Long personId) {
        Ticket ticket = port.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.close(personId);

        port.save(ticket);
    }
}
