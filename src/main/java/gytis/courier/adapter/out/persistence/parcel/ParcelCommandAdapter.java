package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.application.port.out.parcel.ParcelCommandPort;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.order.Parcel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ParcelCommandAdapter implements ParcelCommandPort {
    private final ParcelJpaRepository repository;
    private final ParcelJpaMapper mapper;

    public ParcelCommandAdapter(ParcelJpaRepository repository, ParcelJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Parcel> find(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Parcel update(Parcel parcel) {
            ParcelJpaEntity managedEntity = repository.findById(parcel.getId()).orElseThrow();
        mapper.updateEntity(parcel, managedEntity);

        return parcel;
    }

    @Transactional
    @Override
    public int changeStatuses(Map<ParcelStatus, List<Long>> groupedIdsByStatuses) {
        int total = 0;
        for (Map.Entry<ParcelStatus, List<Long>> entry : groupedIdsByStatuses.entrySet()) {
            total += repository.updateStatusByIds(entry.getKey(), entry.getValue());
        }
        return total;
    }

    @Transactional
    @Override
    public int markAssigned(List<Long> parcelIds) {
        return repository.markAssigned(parcelIds);
    }

    @Transactional
    @Override
    public int markUnassigned(List<Long> parcelIds) {
        return repository.markUnassigned(parcelIds);
    }

    @Override
    public int updateStatus(Long parcelId, ParcelStatus parcelStatus) {
        return repository.updateStatus(parcelId, parcelStatus);
    }
}
