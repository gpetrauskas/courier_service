package gytis.courier.adapter.out.persistence.address.orderaddress;

import gytis.courier.adapter.out.persistence.address.common.AddressDetailsJpaMapper;
import gytis.courier.domain.order.OrderAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = { AddressDetailsJpaMapper.class }
)
public interface OrderAddressMapper {
    @Mapping(target = "details", source = "detailsJpa")
    OrderAddress toDomain(OrderAddressJpaEntity entity);
    @Mapping(target = "detailsJpa", source = "details")
    OrderAddressJpaEntity toEntity(OrderAddress domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "detailsJpa", source = "details")
    void updateEntity(OrderAddress domain, @MappingTarget OrderAddressJpaEntity entity);
}
