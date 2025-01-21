package com.example.courier.dto;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.DeliveryTaskItem;

public record DeliveryTaskItemDTO(Long id, PackageDTO packageDTO, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress, PackageStatus status) {

    public static DeliveryTaskItemDTO fromDeliveryTaskItem(DeliveryTaskItem deliveryTaskItem, OrderAddressDTO senderAddress, OrderAddressDTO recipientAddress, PackageStatus status) {

        PackageDTO packageDTO1 = PackageDTO.packageToDTO(deliveryTaskItem.getParcel());


        return new DeliveryTaskItemDTO(deliveryTaskItem.getId(), packageDTO1, senderAddress, recipientAddress, status);
    }
}
