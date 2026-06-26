package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.address.orderaddress.OrderAddressMapper;
import gytis.courier.adapter.out.persistence.parcel.ParcelJpaMapper;
import gytis.courier.application.port.out.order.OrderCommandPort;

import gytis.courier.domain.order.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class OrderAdapter implements OrderCommandPort {
    private final OrderJpaRepository repository;
    private final OrderEntityMapper mapper;
    private final ParcelJpaMapper parcelMapper;
    private final OrderAddressMapper addressMapper;

    public OrderAdapter(OrderJpaRepository repository, OrderEntityMapper mapper, ParcelJpaMapper parcelMapper, OrderAddressMapper addressMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.parcelMapper = parcelMapper;
        this.addressMapper = addressMapper;
    }

    @Transactional
    @Override
    public Order insert(Order order) {
        OrderJpaEntity entity = mapper.toEntity(order);
        OrderJpaEntity saved = repository.save(entity);

        return order.withId(saved.getId());
    }

    @Transactional
    @Override
    public void save(Order order) {
        OrderJpaEntity managed = repository.findWithParcelAndAddressesById(order.getId()).orElseThrow();
        mapper.updateEntityFromDomain(order, managed);
        parcelMapper.updateEntity(order.getParcel(), managed.getParcel());
        addressMapper.updateEntity(order.getSenderAddress(), managed.getSenderAddress());
        addressMapper.updateEntity(order.getRecipientAddress(), managed.getRecipientAddress());
        System.out.println("managed flat after update: " + managed.getSenderAddress().getDetailsJpa().getFlatNumber());
    }


    @Override
    public Optional<Order> getForUser(Long orderId, Long userId) {
        return repository.findWithParcelByIdAndUserId(orderId, userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Order> getWithParcel(Long id) {
        return repository.findWithParcelById(id)
                .map(mapper::toDomainWithParcel);
    }

    @Override
    public Optional<Order> getWithParcelAndAddresses(Long id) {
        return repository.findWithParcelAndAddressesById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Order> findAllByParcelIds(List<Long> ids) {
        List<OrderJpaEntity> orderJpaEntities = repository.findAllByParcelIdIn(ids);
        return orderJpaEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> getBasicById(Long orderId) {
        return repository.findById(orderId)
                .map(mapper::toDomain);
    }
}
