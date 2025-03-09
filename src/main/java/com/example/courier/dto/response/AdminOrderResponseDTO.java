package com.example.courier.dto.response;

import com.example.courier.common.OrderStatus;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.PersonResponseDTO;

import java.time.LocalDateTime;

public record AdminOrderResponseDTO(
        Long id, PersonResponseDTO personResponseDTO, AddressDTO senderAddress,
        AddressDTO recipientAddress, String deliveryPreferences, ParcelDTO parcelResponseDTO,
        OrderStatus orderStatus, LocalDateTime createTime, AdminPaymentResponseDTO adminPaymentResponseDTO
) {

}
