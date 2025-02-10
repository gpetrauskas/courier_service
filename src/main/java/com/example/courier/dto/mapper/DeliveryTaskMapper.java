package com.example.courier.dto.mapper;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Courier;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.DeliveryTaskItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryTaskMapper {

    DeliveryTaskMapper INSTANCE = Mappers.getMapper(DeliveryTaskMapper.class);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "courierDTO", source = "courier", qualifiedByName = "mapCourier")
    @Mapping(target = "itemsList", source = "items", qualifiedByName = "mapItems")
    @Mapping(target = "tType", source = "taskType")
    @Mapping(target = "deliveryTask", source = "deliveryStatus")
    @Mapping(target = "adminId", source = "createdBy.id")
    DeliveryTaskDTO toDeliveryTaskDTO(DeliveryTask deliveryTask);

    @Named("mapCourier")
    default CourierDTO mapCourier(Courier courier) {
        if (courier == null) return null;
        return new CourierDTO(courier.getId(), courier.getName(), courier.getEmail());
    }

    @Named("mapItems")
    default List<DeliveryTaskItemDTO> mapItems(List<DeliveryTaskItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .filter(item -> item.getStatus() != ParcelStatus.REMOVED_FROM_THE_LIST)
                .map(this::toDeliveryTaskItemDTO)
                .toList();
    }

    @Mapping(target = "senderAddress", expression = "java(OrderAddressDTO.fromOrderAddress(deliveryTaskItem.getSenderAddress()))")
    @Mapping(target = "recipientAddress", expression = "java(OrderAddressDTO.fromOrderAddress(deliveryTaskItem.getRecipientAddress()))")
    @Mapping(target = "parcelDTO", expression = "java(ParcelDTO.parcelToDTO(deliveryTaskItem.getParcel()))")
    DeliveryTaskItemDTO toDeliveryTaskItemDTO(DeliveryTaskItem deliveryTaskItem);
}
