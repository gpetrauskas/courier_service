package com.example.courier.service.order.handler;

import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;

/**
 * Interface defining handlers for updating different sections of an order .
 * */
public interface OrderUpdateHandler {
    /**
     * Handles updates to the order section.
     *
     * @param request the {@link OrderSectionUpdateRequest} for the order section.
     * */
    void handle(OrderSectionUpdateRequest request);

    /**
     * Handles updates to the parcel section.
     *
     * @param request the {@link ParcelSectionUpdateRequest} for the parcel section
     * */
    void handle(ParcelSectionUpdateRequest request);

    /**
     * Handles updates to the payment section.
     *
     * @param request the {@link PaymentSectionUpdateRequest} for the payment section.
     * */
    void handle(PaymentSectionUpdateRequest request);

    /**
     * Handles updates to the address section.
     *
     * @param request the {@link AddressSectionUpdateRequest} for the address section.
     * */
    void handle(AddressSectionUpdateRequest request);
}
