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
import com.example.courier.exception.PricingOptionNotFoundException;
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
    private PricingOptionRepository pricingOptionRepository;
    @Autowired
    private OrderAddressRepository orderAddressRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;

    @Transactional
    public void placeOrder(Long id, OrderDTO orderDTO) {
        User user = userService.getUserById(id);

        Address senderAddress = addressService.getAddress(orderDTO.senderAddress(), user);
        Address recipientAddress = addressService.getAddress(orderDTO.recipientAddress(), user);

        OrderAddress orderSenderAddress = createOrderAddress(senderAddress);
        OrderAddress orderRecipientAddress = createOrderAddress(recipientAddress);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Package packageDetails = createPackageFromDTO(orderDTO.packageDetails());
        order.setPackageDetails(packageDetails);

        Payment payment = paymentService.createPayment(order, calculateShippingCost(orderDTO));

        orderRepository.save(order);
        paymentService.savePayment(payment);
    }

    private OrderAddress createOrderAddress(Address address) {
        OrderAddress orderAddress = addressMapper.toOrderAddress(address);
        return orderAddressRepository.saveAndFlush(orderAddress);
    }

    private Order createOrderFromDTO(OrderDTO orderDTO, User user, OrderAddress senderAddress, OrderAddress recipientAddress) {
        Order order = orderMapper.toOrder(orderDTO);
        order.setUser(user);
        order.setSenderAddress(senderAddress);
        order.setRecipientAddress(recipientAddress);
        order.setDeliveryPreferences(getDescriptionById(orderDTO.deliveryPreferences()));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        return order;
    }

    private Package createPackageFromDTO(PackageDTO packageDTO) {
        Package packageDetails = new Package();
        packageDetails.setWeight(getDescriptionById(packageDTO.weight()));
        packageDetails.setDimensions(getDescriptionById(packageDTO.dimensions()));
        packageDetails.setContents(packageDTO.contents());
        packageDetails.setTrackingNumber(UUID.randomUUID().toString());
        packageDetails.setStatus(PackageStatus.WAITING_FOR_PAYMENT);

        return packageDetails;
    }

    private String getDescriptionById(String id) {
        return pricingOptionRepository.findById(Long.parseLong(id))
                .map(PricingOption::getDescription)
                .orElseThrow(() -> new RuntimeException("Pricing option not found."));
    }

    public BigDecimal calculateShippingCost(OrderDTO orderDTO) throws PricingOptionNotFoundException {
        BigDecimal shippingCost = new BigDecimal(0);

        PricingOption deliveryPricingOption = pricingOptionRepository.findById(Long.valueOf(orderDTO.deliveryPreferences()))
                .orElseThrow(() -> new RuntimeException("Delivery preference option was not found."));
        PricingOption weightPricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().weight()))
                .orElseThrow(() -> new RuntimeException("Weight option was not found"));
        PricingOption sizePricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().dimensions()))
                .orElseThrow(() -> new RuntimeException("Size option was not found."));

        BigDecimal deliveryPrice = deliveryPricingOption.getPrice();
        BigDecimal weightPrice = weightPricingOption.getPrice();
        BigDecimal sizePricing = sizePricingOption.getPrice();
        shippingCost = shippingCost.add(deliveryPrice).add(weightPrice).add(sizePricing);

        return shippingCost;
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

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
