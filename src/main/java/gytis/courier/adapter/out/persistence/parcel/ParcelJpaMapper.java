package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.domain.order.Parcel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParcelJpaMapper {

    default Parcel toDomain(ParcelJpaEntity entity) {
        if (entity == null) return null;
        return Parcel.restore(
                entity.getId(),
                entity.getWeightId(),
                entity.getWeightName(),
                entity.getWeightPrice(),
                entity.getDimensionsId(),
                entity.getDimensionsName(),
                entity.getDimensionsPrice(),
                entity.getFailuresCount(),
                entity.getContents(),
                entity.getTrackingNumber(),
                entity.isAssigned(),
                entity.getStatus()
        );
    }


    ParcelJpaEntity toEntity(Parcel domain);

    @Mapping(target = "id", ignore = true)
    void updateEntity(Parcel parcel, @MappingTarget ParcelJpaEntity entity);
}
