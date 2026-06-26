package gytis.courier.application.port.out.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.ticket.AdminTicketReadModel;
import gytis.courier.application.readmodel.ticket.TicketReadModel;

public interface TicketQueryPort {
    PageResult<TicketReadModel> myTickets(Long myId, PageQuery pageQuery);
    PageResult<AdminTicketReadModel> getAll(PageQuery pageQuery);

    boolean existsByIdAndCreatedById(Long ticketId, Long userId);
}
