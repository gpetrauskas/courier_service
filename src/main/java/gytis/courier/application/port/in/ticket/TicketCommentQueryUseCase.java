package gytis.courier.application.port.in.ticket;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.ticket.TicketCommentReadModel;

public interface TicketCommentQueryUseCase {
    PageResult<TicketCommentReadModel> getComments(Long ticketId, Long personId, String isAdmin, PageQuery pageQuery);
}
