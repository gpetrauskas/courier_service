package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.ticket.TicketCommentQueryPort;
import gytis.courier.application.readmodel.ticket.TicketCommentReadModel;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TicketCommentQueryAdapter implements TicketCommentQueryPort {
    private final TicketCommentJpaRepository repository;
    private final TicketReadModelMapper mapper;

    public TicketCommentQueryAdapter(TicketCommentJpaRepository repository, TicketReadModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PageResult<TicketCommentReadModel> getComments(Long ticketId, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);

        return PageResultMapper.from(
                repository.findByTicketId(ticketId, pageable),
                mapper::toComment
        );
    }

/*    @Override
    public PageResult<AdminTicketCommentReadModel> getForAdmin(Long ticketId, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);

        return PageResultMapper.from(
                repository.findByTicketId(ticketId, pageable),
                mapper::toAdminComment
        );
    }*/
}
