package gytis.courier.adapter.out.persistence.delivery;

import gytis.courier.domain.delivery.DeliveryOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeliveryOptionEntityMapper {
    DeliveryOption toDomain(DeliveryOptionJpaEntity entity);
    DeliveryOptionJpaEntity toEntity(DeliveryOption domain);

    @Mapping(target = "id", ignore = true)
    void updateEntity(DeliveryOption domain, @MappingTarget DeliveryOptionJpaEntity entity);





   // DeliveryOptionReadModel toReadModel(DeliveryOptionProjection projection);
}
