package com.example.courier.dto.mapper;

import com.example.courier.domain.Parcel;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParcelMapper {

    @Mapping(target = "id", ignore = true)
    void updateParcelSectionFromRequest(ParcelSectionUpdateRequest parcelSectionUpdateRequest, @MappingTarget Parcel parcel);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "dimensions", target = "dimensions")
    @Mapping(source = "contents", target = "contents")
    @Mapping(source = "trackingNumber", target = "trackingNumber")
    @Mapping(source = "status", target = "status")
    ParcelDTO toParcelDTO(Parcel parcel);
}
