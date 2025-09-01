package com.example.courier.service.order.facade;

import com.example.courier.dto.request.order.*;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.order.command.OrderCommandService;
import com.example.courier.service.order.handler.OrderUpdateHandler;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.payment.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade service that orchestrates updates to various sections of an order.
 * Implements {@link OrderUpdateHandler} to delegate section specific updates to the
 * appropriate service layer.
 *
 * <p>This service is secured for admin access only.</p>
 * */
@Service
public class OrderFacadeService implements OrderUpdateHandler {

    private final OrderCommandService orderCommandService;
    private final ParcelService parcelService;
    private final AddressService addressService;
    private final PaymentService paymentService;

    public OrderFacadeService(OrderCommandService orderCommandService, ParcelService parcelService,
                              AddressService addressService, PaymentService paymentService) {
        this.orderCommandService = orderCommandService;
        this.parcelService = parcelService;
        this.addressService = addressService;
        this.paymentService = paymentService;
    }

    /**
     * Applies an update to a specific section of order.
     * Delegates the update to the appropriate handler based on the request type.
     *
     * @param updatedData the update request containing section specific data.
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateSection(BaseOrderUpdateRequest updatedData) {
        updatedData.applyUpdate(this);
    }

    @Override
    public void handle(OrderSectionUpdateRequest request) {
        orderCommandService.updateOrderSection(request);
    }

    @Override
    public void handle(ParcelSectionUpdateRequest request) {
        parcelService.parcelSectionUpdate(request);
    }

    @Override
    public void handle(AddressSectionUpdateRequest request) {
        addressService.addressSectionUpdate(request);
    }

    @Override
    public void handle(PaymentSectionUpdateRequest request) {
        paymentService.paymentSectionUpdate(request);
    }
}
