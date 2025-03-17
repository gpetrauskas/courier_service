package com.example.courier.service;

import com.example.courier.common.*;
import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.*;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.*;
import com.example.courier.specification.order.OrderSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryOptionRepository deliveryOptionRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private CourierRepository courierRepository;
    private final OrderMapper orderMapper;
    @Autowired
    private PersonMapper personMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public PersonDetailsDTO findPersonById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User was not found"));

        PersonDetailsDTO personDetailsDTO;
        personDetailsDTO = personMapper.toPersonDetailsDTO(person);

        return personDetailsDTO;
    }

    public AdminOrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order was not found."));
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                new PaymentMethodNotFoundException("Payment not found"));
        return orderMapper.toAdminOrderDTO(order, payment);
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

    public DeliveryOptionBaseDTO getDeliveryOptionById(Long id) {
            return deliveryOptionRepository.findById(id)
                    .map(DeliveryOptionBaseDTO::fromDeliveryOption)
                    .orElseThrow(() -> new DeliveryOptionNotFoundException("Delivery option not found."));
    }
}
