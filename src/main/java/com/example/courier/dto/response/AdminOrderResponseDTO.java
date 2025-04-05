package com.example.courier.dto.response;

import com.example.courier.common.OrderStatus;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;

import java.time.LocalDateTime;

public record AdminOrderResponseDTO(
        Long id, AdminPersonResponseDTO adminPersonResponseDTO, AddressDTO senderAddress,
        AddressDTO recipientAddress, String deliveryMethod, ParcelDTO parcelResponseDTO,
        OrderStatus orderStatus, LocalDateTime createTime, AdminPaymentResponseDTO adminPaymentResponseDTO
) {

}
