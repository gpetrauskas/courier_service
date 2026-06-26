package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.adapter.out.persistence.parcel.projection.ParcelProjection;
import gytis.courier.application.readmodel.parcel.ParcelReadModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParcelReadModelMapper {
    @Mapping(target = "weight", source = "weightName")
    @Mapping(target = "dimensions", source = "dimensionsName")
    ParcelReadModel toReadModel(ParcelProjection projection);
}
