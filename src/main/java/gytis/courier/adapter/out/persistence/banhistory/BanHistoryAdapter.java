package gytis.courier.adapter.out.persistence.banhistory;

import gytis.courier.application.port.out.person.BanHistoryPort;
import gytis.courier.application.readmodel.person.BanHistoryReadModel;
import gytis.courier.domain.banhistory.BanHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BanHistoryAdapter implements BanHistoryPort {
    private final BanHistoryJpaRepository repository;
    private final BanHistoryEntityMapper mapper;
    private final BanHistoryReadModelMapper queryMapper;

    public BanHistoryAdapter(BanHistoryJpaRepository repository, BanHistoryEntityMapper mapper, BanHistoryReadModelMapper queryMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryMapper = queryMapper;
    }

    @Override
    public void save(BanHistory banHistory) {
        BanHistoryJpaEntity entity = mapper.toJpaEntity(banHistory);
        repository.save(entity);
    }

    @Override
    public List<BanHistoryReadModel> getBanHistory(Long personId) {
        return repository.findAllByPersonId(personId).stream()
                .map(queryMapper::toReadModel)
                .toList();
    }
}
