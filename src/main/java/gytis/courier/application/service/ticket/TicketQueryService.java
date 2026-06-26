package gytis.courier.application.service.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.ticket.AdminTicketQueryUseCase;
import gytis.courier.application.port.in.ticket.TicketCommentQueryUseCase;
import gytis.courier.application.port.in.ticket.UserTicketQueryUseCase;
import gytis.courier.application.port.out.ticket.TicketCommentQueryPort;
import gytis.courier.application.port.out.ticket.TicketQueryPort;
import gytis.courier.application.readmodel.ticket.AdminTicketReadModel;
import gytis.courier.application.readmodel.ticket.TicketReadModel;
import gytis.courier.application.readmodel.ticket.TicketCommentReadModel;
import gytis.courier.domain.person.Role;
import gytis.courier.exception.ForbiddenException;
import org.springframework.stereotype.Service;

@Service
public class TicketQueryService implements AdminTicketQueryUseCase, UserTicketQueryUseCase, TicketCommentQueryUseCase {
    private final TicketQueryPort queryPort;
    private final TicketCommentQueryPort commentQueryPort;

    public TicketQueryService(TicketQueryPort queryPort, TicketCommentQueryPort commentQueryPort) {
        this.queryPort = queryPort;
        this.commentQueryPort = commentQueryPort;
    }

    public PageResult<TicketReadModel> myTickets(Long myId, PageQuery pageQuery) {
        return queryPort.myTickets(myId, pageQuery);
    }

    public PageResult<AdminTicketReadModel> allTickets(PageQuery pageQuery) {
        return queryPort.getAll(pageQuery);
    }

    public PageResult<TicketCommentReadModel> getComments(Long ticketId, Long personId, String role, PageQuery pageQuery) {
        if (!queryPort.existsByIdAndCreatedById(ticketId, personId) && !Role.ADMIN.equals(Role.valueOf(role))) {
            throw new ForbiddenException("Access denied");
        }

        return commentQueryPort.getComments(ticketId, pageQuery);
    }
}
