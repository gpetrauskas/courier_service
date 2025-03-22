package com.example.courier.service.person;

import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(UserRepository userRepository, OrderRepository orderRepository,
                        PaymentRepository paymentRepository, OrderMapper orderMapper) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderMapper = orderMapper;
    }

    public String generateUserReport() {
        List<User> reportList = userRepository.findAll();

        StringBuilder report = new StringBuilder();
        report.append("User Report\n");
        report.append("ID\tName\tEmail\tAddress\tRole\n");

        for (User user : reportList) {
            report.append(user.getId()).append("\t");
            report.append(user.getName()).append("\t");
            report.append(user.getEmail()).append("\t");
            report.append(user.getAddresses()).append("\t");
        }
        return report.toString();
    }

    public String generateOrderReport() {
        List<Order> orderList = orderRepository.findAll();

        StringBuilder report = new StringBuilder();

        report.append("Order Report\n");
        report.append("Order ID\tUser ID\tSender Address\tRecipient Address\tDeliveryPreferencies\t" +
                "Order Status\tOrder Create Date\tParcel ID\tWeight\tDimensions\tContents\tTracking Number\tParcel Status");

        for (Order order : orderList) {
            Parcel parcelDetails = order.getParcelDetails();
            Payment paymentDetails = paymentRepository.findById(order.getId()).orElse(null);
            report.append(order.getId()).append("\t");
            report.append(order.getUser().getId()).append("\t");
            report.append(order.getSenderAddress()).append("\t");
            report.append(order.getRecipientAddress()).append("\t");
            report.append(order.getDeliveryMethod()).append("\t");
            report.append(order.getStatus()).append("\t");
            report.append(order.getCreateDate()).append("\t");
            report.append(parcelDetails.getId()).append("\t");
            report.append(parcelDetails.getWeight()).append("\t");
            report.append(parcelDetails.getDimensions()).append("\t");
            report.append(parcelDetails.getContents()).append("\t");
            report.append(parcelDetails.getTrackingNumber()).append("\t");
            report.append(parcelDetails.getStatus()).append("\t");
            if (paymentDetails != null) {
                report.append(paymentDetails.getId()).append("\t");
                report.append(paymentDetails.getPaymentMethod()).append("\t");
                report.append(paymentDetails.getAmount()).append("\t");
                report.append(paymentDetails.getStatus()).append("\n");
            } else {
                report.append("N/A\tN/A\tN/A\tN/A\n");
            }
        }
        return report.toString();
    }
}
