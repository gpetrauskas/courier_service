package gytis.courier.application.port.in.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.ticket.TicketReadModel;

public interface UserTicketQueryUseCase {
/*
    PageResult<TicketCommentReadModel> forUser(Long ticketId, Long myId, PageQuery pageQuery);
*/
    PageResult<TicketReadModel> myTickets(Long myId, PageQuery pageQuery);
}
