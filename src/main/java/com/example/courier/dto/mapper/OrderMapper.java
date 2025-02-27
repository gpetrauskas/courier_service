package com.example.courier.dto.mapper;

import com.example.courier.domain.Order;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.OrderSectionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "createTime", source = "createDate")
    OrderDTO toOrderDTO(Order order);

    @Mapping(target = "createDate", source = "createTime")
    @Mapping(target = "senderAddress", ignore = true)
    @Mapping(target = "recipientAddress", ignore = true)
    Order toOrder(OrderDTO orderDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sectionToUpdate", ignore = true)
    void updateOrderSectionFromRequest(OrderSectionUpdateRequest updateRequest, @MappingTarget Order order);
}
