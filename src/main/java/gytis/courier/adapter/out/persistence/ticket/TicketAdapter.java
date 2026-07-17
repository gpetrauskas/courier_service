package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.ticket.projection.TicketProjection;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.ticket.TicketCommandPort;
import gytis.courier.application.port.out.ticket.TicketQueryPort;
import gytis.courier.application.readmodel.ticket.AdminTicketReadModel;
import gytis.courier.application.readmodel.ticket.TicketReadModel;
import gytis.courier.domain.ticket.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TicketAdapter implements TicketQueryPort, TicketCommandPort {
    private final TicketJpaRepository repository;
    private final TicketJpaMapper mapper;
    private final TicketReadModelMapper queryMapper;

    public TicketAdapter(TicketJpaRepository repository, TicketJpaMapper mapper, TicketReadModelMapper queryMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryMapper = queryMapper;
    }

    @Override
    public PageResult<TicketReadModel> myTickets(Long myId, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);

        Page<TicketProjection> list = repository.findAllProjectedByCreatedById(myId, pageable);
        System.out.println("kiek cia " + list.getContent().size());

        return PageResultMapper.from(
                repository.findAllProjectedByCreatedById(myId, pageable),
                queryMapper::toReadModel
        );
    }

    @Override
    public PageResult<AdminTicketReadModel> getAll(PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);

        return PageResultMapper.from(
                repository.findAllProjectedBy(pageable),
                queryMapper::toAdminReadModel
        );
    }

    @Override
    public Optional<Ticket> findById(Long ticketId) {
        return repository.findById(ticketId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void save(Ticket ticket) {
        TicketJpaEntity entity = repository.findById(ticket.getId()).orElseThrow();
        mapper.updateEntity(ticket, entity);
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        TicketJpaEntity entity = mapper.toEntity(ticket);

        repository.save(entity);

        return mapper.toDomain(entity);
    }

    @Override
    public void updateTimestamp(Long ticketId, LocalDateTime timestamp) {
        repository.saveTimestamp(ticketId, timestamp);
    }

    @Override
    public boolean existsByIdAndCreatedById(Long tickedId, Long userId) {
        return repository.existsByIdAndCreatedById(tickedId, userId);
    }

}
