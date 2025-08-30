package com.example.courier.service.order;

import com.example.courier.dto.request.order.*;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.order.command.OrderCommandService;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.payment.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderFacadeService {

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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateSection(BaseOrderUpdateRequest updatedData) {
        switch (updatedData.sectionToEdit()) {
            case "orderSection" -> {
                if (updatedData instanceof OrderSectionUpdateRequest orderSectionUpdateRequest) {
                    orderCommandService.updateOrderSection(orderSectionUpdateRequest);
                }
            }
            case "parcelSection" -> {
                if (updatedData instanceof ParcelSectionUpdateRequest parcelSectionUpdateRequest) {
                    parcelService.parcelSectionUpdate(parcelSectionUpdateRequest);
                }
            }
            case "paymentSection" -> {
                if (updatedData instanceof PaymentSectionUpdateRequest paymentSectionUpdateRequest) {
                    paymentService.paymentSectionUpdate(paymentSectionUpdateRequest);
                }
            }
            case "addressSection" -> {
                if (updatedData instanceof AddressSectionUpdateRequest addressSectionUpdateRequest) {
                    addressService.addressSectionUpdate(addressSectionUpdateRequest);
                }
            }
        }

    }
}
