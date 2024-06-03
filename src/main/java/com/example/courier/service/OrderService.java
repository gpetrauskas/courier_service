package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.mapper.OrderMapper;
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
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PricingOptionRepository pricingOptionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressMapper addressMapper;

    @Transactional
    public void placeOrder(Long id, OrderDTO orderDTO) {
        // fetch user  from the database
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

        // convert orderDOT ti Anress entities
        Address senderAddress = addressMapper.toAddress(orderDTO.senderAddress());
        Address recipientAddress = addressMapper.toAddress(orderDTO.recipientAddress());

        senderAddress.setUser(user);
        recipientAddress.setUser(user);

        // save addresses to the database
        addressRepository.saveAndFlush(senderAddress);
        addressRepository.saveAndFlush(recipientAddress);

        Order order = new Order();
        order.setUser(user);
        order.setSenderAddress(senderAddress);
        order.setRecipientAddress(recipientAddress);

        PricingOption deliveryPreferenceOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.deliveryPreferences()))
                        .orElseThrow(() -> new RuntimeException("Delivery preference option not found."));
        order.setDeliveryPreferences(deliveryPreferenceOption.getDescription());

        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now().withNano(0));

        Package packageDetails = new Package();


        PricingOption weightPricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().weight()))
                        .orElseThrow(() -> new RuntimeException("Weight option not found."));
        packageDetails.setWeight(weightPricingOption.getDescription());

        PricingOption sizePricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().dimensions()))
                        .orElseThrow(() -> new RuntimeException("Size option not found."));
        packageDetails.setDimensions(sizePricingOption.getDescription());

        packageDetails.setContents(orderDTO.packageDetails().contents());
        packageDetails.setTrackingNumber(UUID.randomUUID().toString());
        packageDetails.setStatus(PackageStatus.WAITING_FOR_PAYMENT);

        order.setPackageDetails(packageDetails);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(calculateShippingCost(orderDTO));
        payment.setStatus(PaymentStatus.NOT_PAID);

        orderRepository.save(order);
        paymentRepository.save(payment);

    }

    public BigDecimal calculateShippingCost(OrderDTO orderDTO) {
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
        List<OrderDTO> orderDTOs = orders.stream()
                .map(OrderMapper.INSTANCE::toOrderDTO)
                .collect(Collectors.toList());
        return orderDTOs;
    }

    @Transactional
    public void cancelOrder(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                new RuntimeException("Payment not found."));
        order.setStatus(OrderStatus.CANCELED);
        order.getPackageDetails().setStatus(PackageStatus.CANCELED);
        payment.setStatus(PaymentStatus.CANCELED);

        orderRepository.save(order);
        paymentRepository.save(payment);
    }
}
