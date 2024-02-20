package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import com.example.courier.domain.User;
import com.example.courier.dto.OrderDTO;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PackageRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PackageRepository packageRepository;

    public void placeOrder(Long id, OrderDTO orderDTO, BigDecimal shippingPrice) {
        // fetch user  from the database
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

        Order order = new Order();
        order.setUser(user);
        order.setSenderAddress(orderDTO.senderAddress());
        order.setRecipientAddress(orderDTO.recipientAddress());
        order.setDeliveryPreferences(orderDTO.deliveryPreferences());
        order.setStatus("Pending");
        order.setCreateDate(LocalDateTime.now());

        Package packageDetails = new Package();
        packageDetails.setWeight(orderDTO.packageDetails().getWeight());
        packageDetails.setDimensions(orderDTO.packageDetails().getDimensions());
        packageDetails.setContents(orderDTO.packageDetails().getContents());
        packageDetails.setTrackingNumber(UUID.randomUUID().toString());
        packageDetails.setStatus("Not Shipped");

        order.setPackageDetails(packageDetails);

        orderRepository.save(order);

    }

    public BigDecimal calculateShippingCost(OrderDTO orderDTO) {
        BigDecimal shippingCost = new BigDecimal(0);
        if (orderDTO.deliveryPreferences().equals("express")) {
            shippingCost = shippingCost.add(new BigDecimal(10));
        }
        if (orderDTO.packageDetails().getWeight() > 10) {
            shippingCost.add(new BigDecimal(5));
        }
        return shippingCost;
    }
}
