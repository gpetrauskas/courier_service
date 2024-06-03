package com.example.courier.dto.mapper;

import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO toAddressDTO(Address address);
    Address toAddress(AddressDTO addressDTO);
    void updateAddressFromDTO(AddressDTO addressDTO, @MappingTarget Address address);
}
