package com.example.courier.dto.mapper;

import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.Courier;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryTaskMapper {

    DeliveryTaskMapper INSTANCE = Mappers.getMapper(DeliveryTaskMapper.class);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "courierDTO", source = "courier", qualifiedByName = "mapCourier")
    @Mapping(target = "itemsList", source = "items", qualifiedByName = "mapItems")
    @Mapping(target = "tType", source = "taskType")
    @Mapping(target = "deliveryStatus", source = "deliveryStatus")
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
    @Mapping(target = "parcelDTO", source = "parcel") // Use ParcelMapper to map Parcel to ParcelDTO
    DeliveryTaskItemDTO toDeliveryTaskItemDTO(DeliveryTaskItem deliveryTaskItem);

    // courier task...

    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "taskType", source = "taskType")
    @Mapping(target = "deliveryStatus", source = "deliveryStatus")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "completedAt", source = "completedAt")
    @Mapping(target = "taskItemDTOS", source = "items", qualifiedByName = "mapDeliveryTaskItems")
    CourierTaskDTO toCourierTaskDTO(DeliveryTask deliveryTask, @Context TaskType taskType);

    @Named("mapDeliveryTaskItems")
    default List<CourierTaskItemDTO> mapDeliveryTaskItems(List<DeliveryTaskItem> items, @Context TaskType taskType) {
        if (items == null) return List.of();
        return items.stream()
                .map(i -> toCourierTaskItemDTO(i, taskType))
                .toList();
    }

    @Named("toCourierTaskItemDTO")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "weight", source = "parcel.weight")
    @Mapping(target = "dimensions", source = "parcel.dimensions")
    @Mapping(target = "contents", source = "parcel.contents")
    @Mapping(target = "deliveryPreference", source = "deliveryPreference")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "customerAddress", expression = "java(getCustomerAddress(item, taskType))")
    CourierTaskItemDTO toCourierTaskItemDTO(DeliveryTaskItem item, @Context TaskType taskType);

    default OrderAddressDTO getCustomerAddress(DeliveryTaskItem item, TaskType taskType) {
        if (item == null) return null;
        OrderAddress orderAddress = (taskType == TaskType.PICKUP ?
                item.getSenderAddress() : item.getRecipientAddress());
        return OrderAddressDTO.fromOrderAddress(orderAddress);
    }
}