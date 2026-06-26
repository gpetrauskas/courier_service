package gytis.courier.application.port.in.ticket;

import gytis.courier.application.command.AddTicketCommentCommand;
import gytis.courier.application.command.CreateTicketCommand;
import gytis.courier.application.command.UpdateTicketCommand;

public interface TicketCommandUseCase {
    void addComment(AddTicketCommentCommand command);
    void close(Long ticketId, Long personId);
    void create(CreateTicketCommand command);
    void update(Long ticketId, UpdateTicketCommand command);

}
