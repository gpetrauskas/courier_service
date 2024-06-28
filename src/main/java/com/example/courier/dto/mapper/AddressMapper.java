package com.example.courier.dto.mapper;

import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO toAddressDTO(Address address);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressDTO addressDTO);

    @Mapping(target = "id", ignore = true)
    OrderAddress toOrderAddress(Address address);

    void updateAddressFromDTO(AddressDTO addressDTO, @MappingTarget Address address);
}
