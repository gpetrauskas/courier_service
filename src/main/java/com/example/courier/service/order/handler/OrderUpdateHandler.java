package com.example.courier.service.order.handler;

import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;

public interface OrderUpdateHandler {
    void handle(OrderSectionUpdateRequest request);
    void handle(ParcelSectionUpdateRequest request);
    void handle(PaymentSectionUpdateRequest request);
    void handle(AddressSectionUpdateRequest request);
}
