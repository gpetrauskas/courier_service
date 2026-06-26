package gytis.courier.application.port.out.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.ticket.TicketCommentReadModel;

public interface TicketCommentQueryPort {
/*    PageResult<TicketCommentReadModel> getForUser(Long ticketId, PageQuery pageQuery);
    PageResult<AdminTicketCommentReadModel> getForAdmin(Long ticketId, PageQuery pageQuery);*/
    PageResult<TicketCommentReadModel> getComments(Long ticketId, PageQuery pageQuery);
}
