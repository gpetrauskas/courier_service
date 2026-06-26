package gytis.courier.application.port.in.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.ticket.AdminTicketReadModel;

public interface AdminTicketQueryUseCase {
    PageResult<AdminTicketReadModel> allTickets(PageQuery pageQuery);
/*
    PageResult<AdminTicketCommentReadModel> forAdmin(Long ticketId, PageQuery pageQuery);
*/
}
