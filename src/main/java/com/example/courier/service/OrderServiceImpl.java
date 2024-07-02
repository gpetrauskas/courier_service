package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PackageDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public void placeOrder(Long id, OrderDTO orderDTO) {
        User user = userService.getUserById(id);

        Address senderAddress = addressService.getAddress(orderDTO.senderAddress(), user);
        Address recipientAddress = addressService.getAddress(orderDTO.recipientAddress(), user);

        OrderAddress orderSenderAddress = addressService.createOrderAddressFromAddress(senderAddress);
        OrderAddress orderRecipientAddress = addressService.createOrderAddressFromAddress(recipientAddress);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Package packageDetails = createPackageFromDTO(orderDTO.packageDetails());
        order.setPackageDetails(packageDetails);

        BigDecimal amount = pricingOptionService.calculateShippingCost(orderDTO);

        paymentService.createAndSavePayment(order, amount);
        saveOrder(order);
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
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .toList();
    }

    @Transactional
    public void cancelOrder(Order order) {
        Payment payment = paymentService.getPaymentByOrderId(order.getId());
        order.setStatus(OrderStatus.CANCELED);
        order.getPackageDetails().setStatus(PackageStatus.CANCELED);
        payment.setStatus(PaymentStatus.CANCELED);

        saveOrder(order);
        paymentService.savePayment(payment);
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