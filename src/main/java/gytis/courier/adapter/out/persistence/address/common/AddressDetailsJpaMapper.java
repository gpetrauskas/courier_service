package gytis.courier.adapter.out.persistence.address.common;

import gytis.courier.adapter.out.persistence.address.AddressDetailsJpa;
import gytis.courier.domain.address.AddressDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressDetailsJpaMapper {
    AddressDetailsJpa toJpa(AddressDetails domain);
    AddressDetails toDomain(AddressDetailsJpa jpa);
}
