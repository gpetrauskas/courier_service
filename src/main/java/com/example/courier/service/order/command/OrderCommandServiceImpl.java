package com.example.courier.service.order.command;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.exception.OrderCancellationException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.payment.PaymentService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.DeliveryOptionValidator;
import com.example.courier.validation.adminorderupdate.OrderUpdateValidator;
import com.example.courier.validation.order.OrderCreationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final Logger logger = LoggerFactory.getLogger(OrderCommandServiceImpl.class);
    private final OrderQueryService queryService;
    private final DeliveryOptionValidator deliveryOptionValidator;
    private final OrderRepository repository;
    private final OrderUpdateValidator orderUpdateValidator;
    private final OrderMapper mapper;
    private final OrderCreationValidator creationValidator;
    private final CurrentPersonService currentPersonService;
    private final AddressService addressService;
    private final DeliveryMethodService deliveryMethodService;
    private final PaymentService paymentService;

    public OrderCommandServiceImpl(OrderQueryService queryService, DeliveryOptionValidator deliveryOptionValidator,
                                   OrderUpdateValidator orderUpdateValidator, OrderMapper mapper,
                                   OrderRepository repository, OrderCreationValidator creationValidator,
                                   CurrentPersonService currentPersonService, AddressService addressService,
                                   DeliveryMethodService deliveryMethodService, PaymentService paymentService) {
        this.queryService = queryService;
        this.deliveryOptionValidator = deliveryOptionValidator;
        this.orderUpdateValidator = orderUpdateValidator;
        this.mapper = mapper;
        this.repository = repository;
        this.creationValidator = creationValidator;
        this.currentPersonService = currentPersonService;
        this.addressService = addressService;
        this.deliveryMethodService = deliveryMethodService;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateOrderSection(OrderSectionUpdateRequest updateRequest) {
        Order order = queryService.fetchById(updateRequest.id());

        deliveryOptionValidator.validateDeliveryPrefMethodUpdate(updateRequest, order);
        orderUpdateValidator.validateOrderSectionStatusUpdate(updateRequest, order);

        mapper.updateOrderSectionFromRequest(updateRequest, order);
        save(order);
    }

    @Override
    @Transactional
    public Map<String, String> placeOrder(OrderDTO orderDTO) {
        creationValidator.validate(orderDTO);
        User user = currentPersonService.getCurrentPersonAs(User.class);

        Order order = createNewOrderFromDTO(orderDTO, user);
        BigDecimal amountToPay = deliveryMethodService.calculateShippingCost(orderDTO);

        save(order);
        paymentService.createPayment(order, amountToPay);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order was placed successfully. Order cost: " + amountToPay);
        response.put("cost", String.valueOf(amountToPay));
        response.put("orderId", String.valueOf(order.getId()));

        return response;
    }


    @Override
    public void cancelOrder(Long orderId) {
        Order order = queryService.fetchById(orderId);
        Payment payment = paymentService.getPaymentByOrderIdAndUserId(order.getId());

        checkIfOrderValidToCancel(order);
        handleOrderCancel(order, payment);

        paymentService.savePayment(payment);
    }





    @Override
    public void save(Order order) {
        repository.save(order);
    }

    private OrderAddress getOrderAddress(AddressDTO addressDTO, User user) {
        return addressService.fetchOrCreateOrderAddress(addressDTO, user);
    }

    private Order createNewOrderFromDTO(OrderDTO orderDTO, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryMethod(getDeliveryOptionDescription(orderDTO.deliveryMethod()));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        setAddress(orderDTO, user, order);
        createParcelDetails(orderDTO.parcelDetails(), order);

        return order;
    }

    private String getDeliveryOptionDescription(String id) {
        return deliveryMethodService.getDescriptionById(Long.parseLong(id));
    }

    private void createParcelDetails(ParcelDTO parcelDTO, Order order) {
        Parcel parcel = new Parcel();
        parcel.setWeight(getDeliveryOptionDescription(parcelDTO.weight()));
        parcel.setDimensions(getDeliveryOptionDescription(parcelDTO.dimensions()));
        parcel.setContents(parcelDTO.contents());
        parcel.setTrackingNumber(UUID.randomUUID().toString());
        parcel.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);

        order.setParcelDetails(parcel);
    }

    private void setAddress(OrderDTO orderDTO, User user, Order order) {
        OrderAddress senderAddress = getOrderAddress(orderDTO.senderAddress(), user);
        OrderAddress recipientAddress = getOrderAddress(orderDTO.recipientAddress(), user);

        order.setSenderAddress(senderAddress);
        order.setRecipientAddress(recipientAddress);
    }

    private void checkIfOrderValidToCancel(Order order) {
        if (order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new OrderCancellationException("Order already confirmed and paid for. " +
                    "Contact support for more information how to abort it.");
        }

        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new OrderCancellationException("Order already canceled.");
        }
    }

    private void handleOrderCancel(Order order, Payment payment) {
        order.setStatus(OrderStatus.CANCELED);
        order.getParcelDetails().setStatus(ParcelStatus.CANCELED);
        payment.setStatus(PaymentStatus.CANCELED);
    }

}
