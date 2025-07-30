package com.example.courier.dto.mapper;

import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.validation.person.PhoneValidator;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO toAddressDTO(Address address);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressDTO addressDTO);

    OrderAddress toOrderAddress(Address address);

    @Mapping(target = "id", ignore = true)
    void updateAddressFromDTO(AddressDTO addressDTO, @MappingTarget Address address);

    @Mapping(target = "id", ignore = true)
    void updateAddressSectionFromRequest(AddressSectionUpdateRequest addressSectionUpdateRequest, @MappingTarget OrderAddress orderAddress);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", expression = "java(phoneValidator.format(addressDTO.phoneNumber()))")
    void updateNameAndPhoneOnly(AddressDTO addressDTO, @MappingTarget Address address, @Context PhoneValidator phoneValidator);


}
