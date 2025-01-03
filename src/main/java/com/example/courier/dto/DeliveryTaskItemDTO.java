package com.example.courier.dto;

import com.example.courier.domain.DeliveryTaskItem;

public record DeliveryTaskItemDTO(Long id, PackageDTO packageDTO, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress) {

    public static DeliveryTaskItemDTO fromDeliveryTaskItem(DeliveryTaskItem deliveryTaskItem, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress) {

        PackageDTO packageDTO1 = PackageDTO.packageToDTO(deliveryTaskItem.getParcel());


        return new DeliveryTaskItemDTO(deliveryTaskItem.getId(), packageDTO1, senderAddress, recipientAddress);
    }
}
