package gytis.courier.application.service.ticket;

import gytis.courier.application.command.AddTicketCommentCommand;
import gytis.courier.application.command.CreateTicketCommand;
import gytis.courier.application.command.UpdateTicketCommand;
import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
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
    private final ActivityLogUseCase logUseCase;

    public TicketCommandService(TicketCommandPort port, TicketCommentCommandPort commentPort, ActivityLogUseCase logUseCase) {
        this.port = port;
        this.commentPort = commentPort;
        this.logUseCase = logUseCase;
    }

    @Override
    public void create(CreateTicketCommand command) {
        System.out.println(command.ticketPriority() + " " + command.description() + " " + command.personId() +
                " " + command.title());
        Ticket ticket = Ticket.create(command);
        Ticket withIdTicket = port.create(ticket);

        logUseCase.saveLog("USER", "ticket created", "User #" + command.personId() + " created new Ticket #" + withIdTicket.getId() + " priority " + ticket.getPriority());
    }

    public void addComment(AddTicketCommentCommand command) {
        Ticket ticket = port.findById(command.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        boolean isAdmin = "ADMIN".equals(command.role());
        TicketComment comment = ticket.addComment(command.personId(), isAdmin, command.message());

        commentPort.save(ticket.getId(), comment);

        port.updateTimestamp(ticket.getId(), ticket.getUpdatedAt());

        logUseCase.saveLog(command.role(), "comment added", "New comment added to a Ticket #" + ticket.getId());
    }

    @Override
    public void update(Long ticketId, UpdateTicketCommand command) {
        Ticket ticket = port.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        command.status().ifPresent(ticket::changeStatus);
        command.priority().ifPresent(ticket::changePriority);

        port.save(ticket);

        logUseCase.saveLog("ADMIN", "ticket update", "");
    }

    @Override
    public void close(Long ticketId, Long personId) {
        Ticket ticket = port.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.close(personId);

        port.save(ticket);

        logUseCase.saveLog("USER", "close ticket", "Ticket #" + ticket.getId() + " was closed");
    }
}
