package gytis.courier.adapter.out.persistence.delivery;

import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.application.port.out.delivery.DeliveryOptionQueryPort;
import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class DeliveryOptionAdapter implements DeliveryOptionCommandPort, DeliveryOptionQueryPort {
    private final DeliveryOptionJpaRepository repository;
    private final DeliveryOptionEntityMapper mapper;
    private final DeliveryOptionReadModelMapper readModelMapper;

    public DeliveryOptionAdapter(DeliveryOptionJpaRepository repository, DeliveryOptionEntityMapper mapper, DeliveryOptionReadModelMapper readModelMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.readModelMapper = readModelMapper;
    }

    // query

    @Override
    public Optional<DeliveryOptionReadModel> findByIdReadModel(Long id) {
        return repository.findProjectedById(id)
                .map(readModelMapper::toReadModel);
    }

    @Override
    public List<DeliveryOptionReadModel> findEnabled() {
        return repository.findAllByDisabledFalse().stream()
                .map(readModelMapper::toReadModel)
                .toList();
    }

    @Override
    public List<DeliveryOptionReadModel> findAll() {
        return repository.findAllBy().stream()
                .map(readModelMapper::toReadModel)
                .toList();
    }

    //command

    @Override
    public DeliveryOption findById(Long id) {
        return mapper.toDomain(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery method was not found")));
    }

    @Override
    public Optional<DeliveryOption> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void save(DeliveryOption option) {
        DeliveryOptionJpaEntity entity = repository.findById(option.id()).orElseThrow();
        mapper.updateEntity(option, entity);
    }

    @Override
    @Transactional
    public void create(DeliveryOption deliveryOption) {
        repository.save(mapper.toEntity(deliveryOption));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
