package com.example.courier.dto;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.DeliveryTaskItem;

public record DeliveryTaskItemDTO(Long id, ParcelDTO parcelDTO, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress, ParcelStatus status) {

    public static DeliveryTaskItemDTO fromDeliveryTaskItem(DeliveryTaskItem deliveryTaskItem, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress, ParcelStatus status) {

        ParcelDTO parcelDTO1 = ParcelDTO.parcelToDTO(deliveryTaskItem.getParcel());


        return new DeliveryTaskItemDTO(deliveryTaskItem.getId(), parcelDTO1, senderAddress, recipientAddress, status);
    }
}
