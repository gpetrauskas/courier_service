package com.example.courier.dto.mapper;

import com.example.courier.domain.Parcel;
import com.example.courier.dto.request.ParcelSectionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParcelMapper {

    @Mapping(target = "id", ignore = true)
    void updateParcelSectionFromRequest(ParcelSectionUpdateRequest parcelSectionUpdateRequest, @MappingTarget Parcel parcel);

}
