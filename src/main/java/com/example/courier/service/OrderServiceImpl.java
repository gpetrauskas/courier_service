package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PackageDTO;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.OrderCancellationException;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;
    @Autowired
    private  PricingOptionService pricingOptionService;

    @Transactional
    public Long placeOrder(Long userId, OrderDTO orderDTO) {
        User user = userService.getUserById(userId);

        OrderAddress orderSenderAddress = getOrderAddress(orderDTO.senderAddress(), user);
        OrderAddress orderRecipientAddress = getOrderAddress(orderDTO.recipientAddress(), user);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Package packageDetails = createPackageFromDTO(orderDTO.packageDetails());
        order.setPackageDetails(packageDetails);

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

    private Package createPackageFromDTO(PackageDTO packageDTO) {
        Package packageDetails = new Package();
        packageDetails.setWeight(getPricingOptionDescription(packageDTO.weight()));
        packageDetails.setDimensions(getPricingOptionDescription(packageDTO.dimensions()));
        packageDetails.setContents(packageDTO.contents());
        packageDetails.setTrackingNumber(UUID.randomUUID().toString());
        packageDetails.setStatus(PackageStatus.WAITING_FOR_PAYMENT);

        return packageDetails;
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
        order.getPackageDetails().setStatus(PackageStatus.CANCELED);
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
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    public List<OrderDTO> findAllOrders() {
        List<Order> allOrders = orderRepository.findAll();

        List<OrderDTO> allOrdersDTO = allOrders.stream()
                .map(OrderMapper.INSTANCE::toOrderDTO)
                .toList();

        return allOrdersDTO;
    }
}