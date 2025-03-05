package com.example.courier.dto.mapper;

import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.OrderAddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderAddressMapper {

    @Mapping(target = "id", ignore = true)
    OrderAddressDTO toOrderAddressDTO(OrderAddress orderAddress);

}
