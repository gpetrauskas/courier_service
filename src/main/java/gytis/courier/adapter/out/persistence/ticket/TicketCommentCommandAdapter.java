package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.application.port.out.ticket.TicketCommentCommandPort;
import gytis.courier.domain.ticket.TicketComment;
import org.springframework.stereotype.Component;

@Component
public class TicketCommentCommandAdapter implements TicketCommentCommandPort {
    private final TicketCommentJpaRepository repository;
    private final TicketJpaMapper mapper;

    public TicketCommentCommandAdapter(TicketCommentJpaRepository repository, TicketJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Long ticketId, TicketComment comment) {
        TicketCommentJpaEntity entity = mapper.toEntity(comment);
        entity.setTicketId(ticketId);
        repository.save(entity);
    }
}
