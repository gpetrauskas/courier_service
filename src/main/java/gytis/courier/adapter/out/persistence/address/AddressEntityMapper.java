package gytis.courier.adapter.out.persistence.address;

import gytis.courier.adapter.out.persistence.address.common.AddressDetailsJpaMapper;
import gytis.courier.domain.address.Address;
import gytis.courier.domain.address.AddressDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { AddressDetailsJpaMapper.class })
public interface AddressEntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateEntity(Address address, @MappingTarget AddressJpaEntity entity);

    default Address toDomain(AddressJpaEntity entity) {
        if (entity == null) return null;

        return Address.restore(
                entity.getId(),
                map(entity.getDetails())
        );
    }

    AddressJpaEntity toEntity(Address domain);

    AddressDetails map(AddressDetailsJpa detailsJpa);
}
