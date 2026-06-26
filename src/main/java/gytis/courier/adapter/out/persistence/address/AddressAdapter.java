package gytis.courier.adapter.out.persistence.address;

import gytis.courier.application.port.out.address.AddressQueryPort;
import gytis.courier.application.readmodel.address.AddressReadModel;
import gytis.courier.domain.address.Address;
import gytis.courier.application.port.out.address.AddressPersistencePort;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class AddressAdapter implements AddressPersistencePort, AddressQueryPort {
    private final AddressJpaRepository repository;
    private final AddressEntityMapper mapper;
    private final AddressReadModelMapper readModelMapper;

    public AddressAdapter(AddressJpaRepository repository, AddressEntityMapper mapper, AddressReadModelMapper readModelMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.readModelMapper = readModelMapper;
    }

    @Override
    public Optional<Address> findByIdAndUserId(Long addressId, Long userId) {
        return repository.findByIdAndUserId(addressId, userId).map(mapper::toDomain);
    }

    @Override
    public Address findById(Long addressId) {
        return repository.findById(addressId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Address now found"));
    }

    @Override
    @Transactional
    public Address save(Address address) {
        AddressJpaEntity entity = repository.findById(address.getId()).orElseThrow();
        mapper.updateEntity(address, entity);

        repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Address create(Address address) {
        AddressJpaEntity entity = mapper.toEntity(address);
        repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long addressId) {
        repository.deleteById(addressId);
    }

    @Override
    public boolean addressIdOwnedByUserId(Long addressId, Long userId) {
        return repository.addressBelongToUser(addressId, userId);
    }

    @Override
    public List<AddressReadModel> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(readModelMapper::toReadModel)
                .toList();
    }
}
