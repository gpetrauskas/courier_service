/*
package com.example.courier.dto.response.order;

import com.example.courier.common.OrderStatus;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;

import java.time.LocalDateTime;

public sealed interface BaseOrderDTO<T extends AddressDTO> permits OrderDTO, AdminOrderDTO {
    Long id();
    T senderAddress();
    T recipientAddress();
    ParcelDTO parcelDetails();
    String deliveryMethod();
    OrderStatus status();
    LocalDateTime createTime();
}
*/
