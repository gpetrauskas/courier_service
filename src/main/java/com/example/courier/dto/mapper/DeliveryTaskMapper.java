package com.example.courier.dto.mapper;

import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.*;
import com.example.courier.dto.*;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.response.task.AdminTaskDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {DeliveryMethodMapper.class})
public interface DeliveryTaskMapper {

    DeliveryTaskMapper INSTANCE = Mappers.getMapper(DeliveryTaskMapper.class);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "courierDTO", source = "courier", qualifiedByName = "mapCourier")
    @Mapping(target = "itemsList", source = "items", qualifiedByName = "mapItems")
    @Mapping(target = "taskType", source = "taskType")
    @Mapping(target = "deliveryStatus", source = "deliveryStatus")
    @Mapping(target = "adminId", source = "createdBy.id")
    AdminTaskDTO toDeliveryTaskDTO(Task task);

    @Named("mapCourier")
    default CourierDTO mapCourier(Courier courier) {
        if (courier == null) return null;
        return new CourierDTO(courier.getId(), courier.getName(), courier.getEmail(), courier.hasActiveTask());
    }

    @Named("mapItems")
    default List<DeliveryTaskItemDTO> mapItems(List<TaskItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .filter(item -> item.getStatus() != ParcelStatus.REMOVED_FROM_THE_LIST)
                .map(this::toDeliveryTaskItemDTO)
                .toList();
    }

    @Mapping(target = "senderAddress", expression = "java(OrderAddressDTO.fromOrderAddress(taskItem.getSenderAddress()))")
    @Mapping(target = "recipientAddress", expression = "java(OrderAddressDTO.fromOrderAddress(taskItem.getRecipientAddress()))")
    @Mapping(target = "parcelDTO", source = "parcel") // Use ParcelMapper to map Parcel to ParcelDTO
    DeliveryTaskItemDTO toDeliveryTaskItemDTO(TaskItem taskItem);

    // courier task...

    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "taskType", source = "taskType")
    @Mapping(target = "deliveryStatus", source = "deliveryStatus")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "completedAt", source = "completedAt")
    @Mapping(target = "taskItemDTOS", source = "items", qualifiedByName = "mapDeliveryTaskItems")
    CourierTaskDTO toCourierTaskDTO(Task task, @Context TaskType taskType);

    @Named("mapDeliveryTaskItems")
    default List<CourierTaskItemDTO> mapDeliveryTaskItems(List<TaskItem> items, @Context TaskType taskType) {
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
    CourierTaskItemDTO toCourierTaskItemDTO(TaskItem item, @Context TaskType taskType);

    default OrderAddressDTO getCustomerAddress(TaskItem item, TaskType taskType) {
        if (item == null) return null;
        OrderAddress orderAddress = (taskType == TaskType.PICKUP ?
                item.getSenderAddress() : item.getRecipientAddress());
        return OrderAddressDTO.fromOrderAddress(orderAddress);
    }
}