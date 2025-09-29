package com.example.courier.dto.mapper;

import com.example.courier.domain.Address;
import com.example.courier.domain.AddressDetails;
import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.validation.person.PhoneValidator;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(source = "details.name", target = "name")
    @Mapping(source = "details.street", target = "street")
    @Mapping(source = "details.city", target = "city")
    @Mapping(source = "details.houseNumber", target = "houseNumber")
    @Mapping(source = "details.flatNumber", target = "flatNumber")
    @Mapping(source = "details.postCode", target = "postCode")
    @Mapping(source = "details.phoneNumber", target = "phoneNumber")
    AddressDTO toAddressDTO(Address address);

    AddressDetails toAddressDetails(AddressDTO addressDTO);

    OrderAddress toOrderAddress(Address address);

    @Mapping(target = "id", ignore = true)
    void updateAddressFromDTO(AddressDTO addressDTO, @MappingTarget Address address);

    AddressDetails updateFromDTOAndEntity(Address address, AddressDTO dto);

    @Mapping(target = "id", ignore = true)
    OrderAddress toOrderAddressFromDetails(AddressDetails details);

    @Mapping(target = "id", ignore = true)
    void updateAddressSectionFromRequest(AddressSectionUpdateRequest addressSectionUpdateRequest, @MappingTarget OrderAddress orderAddress);

}
