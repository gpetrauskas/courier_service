package com.example.courier.service.order;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.ParcelDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.request.OrderSectionUpdateRequest;
import com.example.courier.exception.InvalidDeliveryPreferenceException;
import com.example.courier.exception.OrderCancellationException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.*;
import com.example.courier.service.AddressService;
import com.example.courier.service.AuthService;
import com.example.courier.service.PaymentService;
import com.example.courier.service.PricingOptionService;
import com.example.courier.validator.OrderUpdateValidator;
import com.example.courier.validator.PricingOptionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AuthService authService;
    @Autowired
    private PricingOptionService pricingOptionService;
    private final OrderUpdateValidator orderUpdateValidator;
    private final PricingOptionValidator pricingOptionValidator;

    public OrderServiceImpl(OrderMapper orderMapper, OrderUpdateValidator orderUpdateValidator,
                            PricingOptionValidator pricingOptionValidator) {
        this.orderMapper = orderMapper;
        this.orderUpdateValidator = orderUpdateValidator;
        this.pricingOptionValidator = pricingOptionValidator;
    }

    @Transactional
    public void orderSectionUpdate(OrderSectionUpdateRequest updateRequest) {
        Order order = findOrderById(updateRequest.id());

        pricingOptionValidator.validateDeliveryPrefForOrderStatusUpdate(updateRequest, order);
        orderUpdateValidator.validateOrderSectionUpdate(updateRequest, order);

        orderMapper.updateOrderSectionFromRequest(updateRequest, order);
        orderRepository.save(order);
    }






    @Transactional
    public Long placeOrder(Long userId, OrderDTO orderDTO) {
        User user = authService.getUserById(userId);

        OrderAddress orderSenderAddress = getOrderAddress(orderDTO.senderAddress(), user);
        OrderAddress orderRecipientAddress = getOrderAddress(orderDTO.recipientAddress(), user);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Parcel parcelDetails = createParcelFromDTO(orderDTO.parcelDetails());
        order.setParcelDetails(parcelDetails);

        BigDecimal amount = pricingOptionService.calculateShippingCost(orderDTO);

        saveOrder(order);
        paymentService.createPayment(order, amount);

        return order.getId();
    }

    private OrderAddress getOrderAddress(AddressDTO addressDTO, User user) {
        return addressService.fetchOrCreateOrderAddress(addressDTO, user);
    }

    private Order createOrderFromDTO(OrderDTO orderDTO, User user, OrderAddress senderAddress, OrderAddress recipientAddress) {
        Order order = orderMapper.toOrder(orderDTO);
        order.setUser(user);
        order.setSenderAddress(senderAddress);
        order.setRecipientAddress(recipientAddress);
        order.setDeliveryPreferences(getPricingOptionDescription(orderDTO.deliveryPreferences()));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        return order;
    }

    private String getPricingOptionDescription(String id) {
        return pricingOptionService.getDescriptionById(id);
    }

    private Parcel createParcelFromDTO(ParcelDTO parcelDTO) {
        Parcel parcelDetails = new Parcel();
        parcelDetails.setWeight(getPricingOptionDescription(parcelDTO.weight()));
        parcelDetails.setDimensions(getPricingOptionDescription(parcelDTO.dimensions()));
        parcelDetails.setContents(parcelDTO.contents());
        parcelDetails.setTrackingNumber(UUID.randomUUID().toString());
        parcelDetails.setStatus(ParcelStatus.WAITING_FOR_PAYMENT);

        return parcelDetails;
    }


    public List<OrderDTO> findUserOrders(User user) {
        List<Order> orders = user.getOrders();
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .toList();
    }

    @Transactional
    public void cancelOrder(Long orderId, Principal principal) {
        Order order = findOrderById(orderId);
        Payment payment = paymentService.getPaymentByOrderId(order.getId());

        checkIfOrderValidToCancel(order, principal);
        handleOrderCancel(order, payment);

        paymentService.savePayment(payment);
    }

    public OrderDTO findUserOrderDTOById(Long orderId, Principal principal) {
        Order order = findOrderById(orderId);
        checkIfOrderBelongsToUser(order, principal);
        return orderMapper.toOrderDTO(order);
    }

    private void handleOrderCancel(Order order, Payment payment) {
        order.setStatus(OrderStatus.CANCELED);
        order.getParcelDetails().setStatus(ParcelStatus.CANCELED);
        payment.setStatus(PaymentStatus.CANCELED);
    }

    private void checkIfOrderBelongsToUser(Order order, Principal principal) {
        if (!order.getUser().getEmail().equals(principal.getName())) {
            throw new UnauthorizedAccessException("You are not authorized to cancel this oerder");
        }
    }

    private void checkIfOrderValidToCancel(Order order, Principal principal) {
        checkIfOrderBelongsToUser(order, principal);

        if (order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new OrderCancellationException("Order already confirmed and paid for. " +
                    "Contact support for more information how to abort it.");
        }

        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new OrderCancellationException("Order already canceled.");
        }
    }

    private void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<OrderDTO> findAllOrders() {
        List<Order> allOrders = orderRepository.findAll();

        List<OrderDTO> allOrdersDTO = allOrders.stream()
                .map(OrderMapper.INSTANCE::toOrderDTO)
                .toList();

        return allOrdersDTO;
    }
}