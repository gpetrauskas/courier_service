package com.example.courier.service.order;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.*;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.request.order.OrderSectionUpdateRequest;
import com.example.courier.dto.response.AdminOrderResponseDTO;
import com.example.courier.exception.OrderCancellationException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.*;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.payment.PaymentService;
import com.example.courier.service.deliveryoption.DeliveryMethodService;
import com.example.courier.specification.order.OrderSpecificationBuilder;
import com.example.courier.validation.adminorderupdate.OrderUpdateValidator;
import com.example.courier.validation.DeliveryOptionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private DeliveryMethodService deliveryMethodService;
    private final OrderUpdateValidator orderUpdateValidator;
    private final DeliveryOptionValidator deliveryOptionValidator;

    public OrderServiceImpl(OrderMapper orderMapper, OrderUpdateValidator orderUpdateValidator,
                            DeliveryOptionValidator deliveryOptionValidator) {
        this.orderMapper = orderMapper;
        this.orderUpdateValidator = orderUpdateValidator;
        this.deliveryOptionValidator = deliveryOptionValidator;
    }

    @Transactional
    public void orderSectionUpdate(OrderSectionUpdateRequest updateRequest) {
        Order order = findOrderById(updateRequest.id());

        deliveryOptionValidator.validateDeliveryPrefForOrderStatusUpdate(updateRequest, order);
        orderUpdateValidator.validateOrderSectionUpdate(updateRequest, order);

        orderMapper.updateOrderSectionFromRequest(updateRequest, order);
        orderRepository.save(order);
    }

    @Transactional
    public Page<AdminOrderResponseDTO> getAllOrdersForAdmin(int page, int size, String orderStatus, Long id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate"));
        Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecification(orderStatus, id);
        log.info("test status {}", orderStatus);
        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        if (orderPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> orderIds = orderPage.stream()
                .map(Order::getId)
                .toList();

        Map<Long, Payment> paymentMap = paymentService.getPaymentsForOrders(orderIds);

        return orderPage.map(order -> {
            Payment payment = paymentMap.get(order.getId());
            return orderMapper.toAdminOrderResponseDTO(order, payment);
        });

    }

    public PaginatedResponseDTO<OrderDTO> fetchAllTaskOrdersByTaskType(int page, int size, String taskType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createDate"));
        Specification<Order> specification = OrderSpecificationBuilder.buildOrderSpecificationByTaskType(taskType);

        Page<Order> orders = orderRepository.findAll(specification, pageable);
        Page<OrderDTO> orderDTOPage = orders.map(orderMapper::toOrderDTO);

        return new PaginatedResponseDTO<>(
                orderDTOPage.getContent(),
                orderDTOPage.getNumber(),
                orderDTOPage.getTotalElements(),
                orderDTOPage.getTotalPages()
        );
    }

    public AdminOrderDTO getAdminOrderById(Long id) {
        Order order = findOrderById(id);
        Map<Long, Payment> paymentMao = paymentService.getPaymentsForOrders(List.of(id));
        return orderMapper.toAdminOrderDTO(order, paymentMao.get(id));
    }




    @Transactional
    public Long placeOrder(Long userId, OrderDTO orderDTO) {
        User user = authService.getUserById(userId);

        OrderAddress orderSenderAddress = getOrderAddress(orderDTO.senderAddress(), user);
        OrderAddress orderRecipientAddress = getOrderAddress(orderDTO.recipientAddress(), user);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Parcel parcelDetails = createParcelFromDTO(orderDTO.parcelDetails());
        order.setParcelDetails(parcelDetails);

        BigDecimal amount = deliveryMethodService.calculateShippingCost(orderDTO);

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
        order.setDeliveryMethod(getDeliveryOptionDescription(orderDTO.deliveryMethod()));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        return order;
    }

    private String getDeliveryOptionDescription(String id) {
        return deliveryMethodService.getDescriptionById(id);
    }

    private Parcel createParcelFromDTO(ParcelDTO parcelDTO) {
        Parcel parcelDetails = new Parcel();
        parcelDetails.setWeight(getDeliveryOptionDescription(parcelDTO.weight()));
        parcelDetails.setDimensions(getDeliveryOptionDescription(parcelDTO.dimensions()));
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
            throw new UnauthorizedAccessException("You are not authorized to cancel this order.");
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
    
    public List<Order> fetchAllByParcelDetails(List<Parcel> parcels) {
        return orderRepository.findAllByParcelDetails(parcels);
    }
}