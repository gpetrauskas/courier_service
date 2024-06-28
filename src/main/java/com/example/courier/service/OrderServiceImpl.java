package com.example.courier.service;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.PackageStatus;
import com.example.courier.common.PaymentStatus;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.PackageDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.exception.UserNotFoundException;
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
    private OrderAddressRepository orderAddressRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public void placeOrder(Long id, OrderDTO orderDTO) {
        User user = getUserById(id);

        Address senderAddress = getAddress(orderDTO.senderAddress(), user);
        Address recipientAddress = getAddress(orderDTO.recipientAddress(), user);

        OrderAddress orderSenderAddress = createOrderAddress(senderAddress);
        OrderAddress orderRecipientAddress = createOrderAddress(recipientAddress);

        Order order = createOrderFromDTO(orderDTO, user, orderSenderAddress, orderRecipientAddress);
        Package packageDetails = createPackageFromDTO(orderDTO.packageDetails());

        order.setPackageDetails(packageDetails);
        Payment payment = createPayment(order, calculateShippingCost(orderDTO));

        orderRepository.save(order);
        paymentRepository.save(payment);
    }

    private Address getAddress(AddressDTO addressDTO, User user) {
        if (addressDTO.id() != null) {
            Address address = addressRepository.findById(addressDTO.id()).orElseThrow(() ->
                    new RuntimeException("Address not found."));
            validateAddressUser(address, user);
            return address;
        } else {
            return createNewAddress(addressDTO, user);
        }
    }

    private void validateAddressUser(Address address, User user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to the user.");
        }
    }

    private Address createNewAddress(AddressDTO addressDTO, User user) {
        Address address = addressMapper.toAddress(addressDTO);
        address.setUser(user);

        return addressRepository.saveAndFlush(address);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
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

    private Payment createPayment(Order order, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.NOT_PAID);

        return payment;
    }

    private String getDescriptionById(String id) {
        return pricingOptionRepository.findById(Long.parseLong(id))
                .map(PricingOption::getDescription)
                .orElseThrow(() -> new RuntimeException("Pricing option not found."));
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
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .toList();
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
