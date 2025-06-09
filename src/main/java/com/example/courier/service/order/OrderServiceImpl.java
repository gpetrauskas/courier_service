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
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.specification.order.OrderSpecificationBuilder;
import com.example.courier.validation.adminorderupdate.OrderUpdateValidator;
import com.example.courier.validation.DeliveryOptionValidator;
import com.example.courier.validation.order.OrderCreationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final AuthService authService;
    private final DeliveryMethodService deliveryMethodService;
    private final OrderUpdateValidator orderUpdateValidator;
    private final DeliveryOptionValidator deliveryOptionValidator;
    private final OrderCreationValidator orderCreationValidator;
    private final CurrentPersonService currentPersonService;

    public OrderServiceImpl(OrderMapper orderMapper, OrderRepository orderRepository, OrderUpdateValidator orderUpdateValidator,
                            DeliveryOptionValidator deliveryOptionValidator, AddressService addressService,
                            PaymentService paymentService, AuthService authService, DeliveryMethodService deliveryMethodService,
                            OrderCreationValidator orderCreationValidator, CurrentPersonService currentPersonService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.orderUpdateValidator = orderUpdateValidator;
        this.deliveryOptionValidator = deliveryOptionValidator;
        this.addressService = addressService;
        this.authService = authService;
        this.paymentService = paymentService;
        this.deliveryMethodService = deliveryMethodService;
        this.orderCreationValidator = orderCreationValidator;
        this.currentPersonService = currentPersonService;
    }

    @Transactional
    public void orderSectionUpdate(OrderSectionUpdateRequest updateRequest) {
        if (!currentPersonService.isAdmin()) {
            throw new AccessDeniedException("Only ADMIN can update Order section");
        }

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
        log.info("validating dto");
        orderCreationValidator.validate(orderDTO);
        log.info("validated dto");

        Person person = currentPersonService.getCurrentPerson();
        if (!(person instanceof User user)) {
            throw new UnauthorizedAccessException("Not allowed to place orders");
        }

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
        order.setDeliveryMethod(getDeliveryOptionDescription(Long.parseLong(orderDTO.deliveryMethod())));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        return order;
    }

    private String getDeliveryOptionDescription(Long id) {
        return deliveryMethodService.getDescriptionById(id);
    }

    private Parcel createParcelFromDTO(ParcelDTO parcelDTO) {
        Parcel parcelDetails = new Parcel();
        parcelDetails.setWeight(getDeliveryOptionDescription(Long.parseLong(parcelDTO.weight())));
        parcelDetails.setDimensions(getDeliveryOptionDescription(Long.parseLong(parcelDTO.dimensions())));
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
    public void cancelOrder(Long orderId) {
        Order order = findOrderByIdAndUserId(orderId, currentPersonService.getCurrentPersonId());
        Payment payment = paymentService.getPaymentByOrderId(order.getId());

        checkIfOrderValidToCancel(order);
        handleOrderCancel(order, payment);

        paymentService.savePayment(payment);
    }

    public OrderDTO findUserOrderDTOById(Long orderId) {
        Order order = findOrderByIdAndUserId(orderId, currentPersonService.getCurrentPersonId());
        return orderMapper.toOrderDTO(order);
    }

    private Order findOrderByIdAndUserId(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or not owned by the user."));
    }

    private void handleOrderCancel(Order order, Payment payment) {
        order.setStatus(OrderStatus.CANCELED);
        order.getParcelDetails().setStatus(ParcelStatus.CANCELED);
        payment.setStatus(PaymentStatus.CANCELED);
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