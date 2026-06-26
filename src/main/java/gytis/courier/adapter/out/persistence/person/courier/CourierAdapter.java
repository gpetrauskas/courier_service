package gytis.courier.adapter.out.persistence.person.courier;

import gytis.courier.adapter.out.persistence.person.PersonEntityMapper;
import gytis.courier.adapter.out.persistence.person.user.PersonInfoReadModelMapper;
import gytis.courier.application.port.out.person.CourierCommandPort;
import gytis.courier.application.port.out.person.CourierQueryPort;
import gytis.courier.application.readmodel.person.CourierReadModel;
import gytis.courier.domain.person.Courier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class CourierAdapter implements CourierQueryPort, CourierCommandPort {
    private final CourierJpaRepository repository;
    private final PersonInfoReadModelMapper mapper;
    private final PersonEntityMapper entityMapper;

    public CourierAdapter(CourierJpaRepository repository, PersonInfoReadModelMapper mapper, PersonEntityMapper entityMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<CourierReadModel> findAvailableCouriers() {
         return repository.findByHasActiveTaskFalse().stream()
                 .map(mapper::toReadModel)
                 .toList();
    }

    @Override
    public Long getAvailableCouriersCount() {
        return repository.countByHasActiveTaskFalse();
    }

    @Override
    public Optional<Courier> findById(Long id) {
        return repository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public void update(Courier courier) {
        System.out.println("id: " + courier.getId() + "has active task:  " + courier.getHasActiveTask());
        CourierJpaEntity managed = repository.findById(courier.getId()).orElseThrow();
        entityMapper.updateCourier(courier, managed);
    }
}
