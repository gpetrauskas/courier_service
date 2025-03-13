package com.example.courier.dto.mapper;

import com.example.courier.domain.Order;
import com.example.courier.domain.Payment;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
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
    void updateOrderSectionFromRequest(OrderSectionUpdateRequest updateRequest, @MappingTarget Order order);

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.status", target = "status")
    AdminOrderDTO toAdminOrderDTO(Order order, Payment payment);

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.user", target = "personResponseDTO")
    @Mapping(source = "order.senderAddress", target = "senderAddress")
    @Mapping(source = "order.recipientAddress", target = "recipientAddress")
    @Mapping(source = "order.deliveryMethod", target = "deliveryMethod")
    @Mapping(source = "order.parcelDetails", target = "parcelResponseDTO")
    @Mapping(source = "order.status", target = "orderStatus")
    @Mapping(source = "order.createDate", target = "createTime")
    @Mapping(source = "payment", target = "adminPaymentResponseDTO")
    AdminOrderResponseDTO toAdminOrderResponseDTO(Order order, Payment payment);
}
