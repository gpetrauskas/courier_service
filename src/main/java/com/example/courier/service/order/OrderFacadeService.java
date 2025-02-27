package com.example.courier.service.order;

import com.example.courier.dto.request.BaseOrderUpdateRequest;
import com.example.courier.dto.request.OrderSectionUpdateRequest;
import com.example.courier.service.AddressService;
import com.example.courier.service.ParcelService;
import com.example.courier.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderFacadeService {

    private final OrderService orderService;
    private final ParcelService parcelService;
    private final AddressService addressService;
    private final PaymentService paymentService;

    public OrderFacadeService(OrderService orderService, ParcelService parcelService,
                              AddressService addressService, PaymentService paymentService) {
        this.orderService = orderService;
        this.parcelService = parcelService;
        this.addressService = addressService;
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateSection(BaseOrderUpdateRequest updatedData) {
        switch (updatedData.sectionToEdit()) {
            case "orderSection" -> orderService.orderSectionUpdate((OrderSectionUpdateRequest) updatedData);
            case "parcelSection" -> parcelService.parcelSectionUpdate(updatedData);
            case "addressSection" -> addressService.addressSectionUpdate(updatedData);
            case "paymentSection" -> paymentService.paymentSectionUpdate(updatedData);
        }

    }
}
