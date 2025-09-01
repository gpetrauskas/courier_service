package com.example.courier.service.order.factory;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory class responsible for creating {@link Order} entities from DTOs.
 * */
@Service
public class OrderFactory {
    private static final Logger logger = LoggerFactory.getLogger(OrderFactory.class);
    private final DeliveryMethodService deliveryMethodService;
    private final AddressService addressService;
    private final CurrentPersonService currentPersonService;

    public OrderFactory(DeliveryMethodService deliveryMethodService, AddressService addressService,
                        CurrentPersonService currentPersonService) {
        this.deliveryMethodService = deliveryMethodService;
        this.addressService = addressService;
        this.currentPersonService = currentPersonService;
    }

    /**
     * Creates a new {@link Order} entity from the provided DTO.
     *
     * @param dto the {@link OrderDTO} containing user input
     * @return an order
     * */
    public Order createNewOrderFromDTO(OrderDTO dto) {
        User user = currentPersonService.getCurrentPersonAs(User.class);

        System.out.println(dto.preference());
        System.out.println(dto.parcelDetails());

        Order order = new Order();
        order.setUser(user);
        order.setPreference(getDeliveryMethodDescriptionById(dto.preference()));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        setAddress(dto, user, order);
        createAndSetParcel(dto.parcelDetails(), order);

        return order;
    }

    /**
     * Sets sender and recipient addresses to the order.
     * */
    private void setAddress(OrderDTO dto, User user, Order order) {
        OrderAddress senderAddress = getOrderAddress(dto.senderAddress(), user);
        OrderAddress recipientAddress = getOrderAddress(dto.recipientAddress(), user);

        order.setSenderAddress(senderAddress);
        order.setRecipientAddress(recipientAddress);
    }

    /**
     * Creates and attaches a parcel to the order.
     * */
    private void createAndSetParcel(ParcelDTO dto, Order order) {
        Parcel parcel = new Parcel();
        parcel.setContents(dto.contents());
        parcel.setDimensions(getDeliveryMethodDescriptionById(dto.dimensions()));
        parcel.setWeight(getDeliveryMethodDescriptionById(dto.weight()));
        parcel.setTrackingNumber(UUID.randomUUID().toString());
        parcel.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);

        order.setParcelDetails(parcel);
    }

    /**
     * Fetches a {@link DeliveryMethod} by id.
     * */
    private DeliveryMethod getDeliveryMethodDescriptionById(String id) {
        return deliveryMethodService.getDeliveryOptionById(Long.parseLong(id));
    }

    /**
     * Retrieves or create an {@link OrderAddress} for the given DTO and user.
     * */
    private OrderAddress getOrderAddress(AddressDTO dto, User user) {
        return addressService.fetchOrCreateOrderAddress(dto, user);
    }
}
