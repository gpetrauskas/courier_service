package com.example.courier.service;

import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.OrderDTO;
import com.example.courier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private PricingOptionRepository pricingOptionRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public void placeOrder(Long id, OrderDTO orderDTO, BigDecimal shippingPrice) {
        // fetch user  from the database
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

        Order order = new Order();
        order.setUser(user);
        order.setSenderAddress(orderDTO.senderAddress());
        order.setRecipientAddress(orderDTO.recipientAddress());

        PricingOption deliveryPreferenceOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.deliveryPreferences()))
                        .orElseThrow(() -> new RuntimeException("Delivery preference option not found."));
        order.setDeliveryPreferences(deliveryPreferenceOption.getDescription());

        order.setStatus("Pending");
        order.setCreateDate(LocalDateTime.now());

        Package packageDetails = new Package();

        PricingOption weightPricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().getWeight()))
                        .orElseThrow(() -> new RuntimeException("Weight option not found."));
        packageDetails.setWeight(weightPricingOption.getDescription());

        PricingOption sizePricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().getDimensions()))
                        .orElseThrow(() -> new RuntimeException("Size option not found."));
        packageDetails.setDimensions(sizePricingOption.getDescription());

        packageDetails.setContents(orderDTO.packageDetails().getContents());
        packageDetails.setTrackingNumber(UUID.randomUUID().toString());
        packageDetails.setStatus("WAITING_FOR_PAYMENT");

        order.setPackageDetails(packageDetails);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(calculateShippingCost(orderDTO));
        payment.setStatus("NOT_PAID");

        orderRepository.save(order);
        paymentRepository.save(payment);

    }

    public BigDecimal calculateShippingCost(OrderDTO orderDTO) {
        BigDecimal shippingCost = new BigDecimal(0);

        PricingOption deliveryPricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.deliveryPreferences()))
                .orElseThrow(() -> new RuntimeException("Delivery preference option was not found."));
        PricingOption weightPricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().getWeight()))
                .orElseThrow(() -> new RuntimeException("Weight option was not found"));
        PricingOption sizePricingOption = pricingOptionRepository.findById(Long.parseLong(orderDTO.packageDetails().getDimensions()))
                .orElseThrow(() -> new RuntimeException("Size option was not found."));

        shippingCost.add(deliveryPricingOption.getPrice());
        shippingCost.add(weightPricingOption.getPrice());
        shippingCost.add(sizePricingOption.getPrice());

        return shippingCost;
    }

    public List<OrderDTO> findUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserId(user.getId());
        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> mapToOrderDTO(order))
                .collect(Collectors.toList());
        return orderDTOs;
    }

    public OrderDTO mapToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO(order.getId(),
                order.getSenderAddress(),
                order.getRecipientAddress(),
                order.getPackageDetails(),
                order.getDeliveryPreferences(),
                order.getStatus(),
                order.getCreateDate());

        return orderDTO;
    }
}
