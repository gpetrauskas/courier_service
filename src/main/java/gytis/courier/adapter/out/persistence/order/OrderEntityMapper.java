package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.address.orderaddress.OrderAddressMapper;
import gytis.courier.adapter.out.persistence.parcel.ParcelJpaMapper;
import gytis.courier.adapter.out.persistence.person.PersonEntityMapper;
import gytis.courier.domain.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {
        PersonEntityMapper.class,
        OrderAddressMapper.class,
        ParcelJpaMapper.class
})
public interface OrderEntityMapper {
    @Mapping(target = "senderAddress", ignore = true)
    @Mapping(target = "recipientAddress", ignore = true)
    Order toDomainWithParcel(OrderJpaEntity entity);

    Order toDomain(OrderJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    OrderJpaEntity toEntity(Order domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderAddress", ignore = true)
    @Mapping(target = "recipientAddress", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    void updateEntityFromDomain(Order domain, @MappingTarget OrderJpaEntity entity);




}
