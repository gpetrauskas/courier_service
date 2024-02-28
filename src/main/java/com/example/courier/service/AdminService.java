package com.example.courier.service;

import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import com.example.courier.domain.Payment;
import com.example.courier.domain.User;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public List<UserResponseDTO> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserResponseDTO> allUserResponseDTOs = allUsers.stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
        return allUserResponseDTOs;
    }

    public Optional<UserResponseDTO> findUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found"));
            UserResponseDTO userResponseDTO = UserResponseDTO.fromUser(user);

            return Optional.of(userResponseDTO);
        } catch (UserNotFoundException e) {
            logger.warn("User not found", e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            logger.error("Error occurred finding user", e);
            throw e;
        }
    }

    public void updateUser(Long id, UserDTO updatedUser) {
        try {
            User existingUser = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found."));
            logger.info("AdminService: updateUser after findById");

            updateUserFields(existingUser, updatedUser);
            userRepository.save(existingUser);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User was not found");
        }
    }

    private void updateUserFields(User existingUse, UserDTO updatedUser) {
        if (updatedUser.name() != null) {
            existingUse.setName(updatedUser.name());
        }
        if (updatedUser.email() != null) {
            existingUse.setEmail(updatedUser.email());
        }
        if (updatedUser.address() != null) {
            existingUse.setAddress(updatedUser.address());
        }
        if (updatedUser.password() != null) {
            String encodedPass = passwordEncoder.encode(updatedUser.password());
            existingUse.setPassword(encodedPass);
        }
    }

    public void deleteUser(Long id) {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found."));
            logger.info("User was found for deletion");
            userRepository.delete(user);
    }

    public List<AdminOrderDTO> getAllOrders() {
        try {
            List<Order> allOrders = orderRepository.findAll();
            List<AdminOrderDTO> allOrderDTOs = allOrders.stream()
                    .map(AdminOrderDTO::fromOrder)
                    .collect(Collectors.toList());
            return allOrderDTOs;
        } catch (Exception e) {
            throw e;
        }
    }

    public AdminOrderDTO getOrderById(Long id) {
        try {
            Order order = orderRepository.findById(id).orElseThrow(() ->
                    new OrderNotFoundException("Order was not found."));
            AdminOrderDTO adminOrderDTO = AdminOrderDTO.fromOrder(order);
            return adminOrderDTO;
        } catch (OrderNotFoundException e) {
            logger.warn("Order was not found for id: " + id);
            return null;
        } catch (Exception e) {
            logger.warn("Error occurred while retrieving order with id: " + id);
            return null;
        }
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
            report.append(user.getAddress()).append("\t");
            report.append(user.getRole()).append("\n");
        }
        return report.toString();
    }

    public String generateOrderReport() {
        List<Order> orderList = orderRepository.findAll();

        StringBuilder report = new StringBuilder();

        report.append("Order Report\n");
        report.append("Order ID\tUser ID\tSender Address\tRecipient Address\tDeliveryPreferencies\t" +
                "Order Status\tOrder Create Date\tPackage ID\tWeight\tDimensions\tContents\tTracking Number\tPackage Status");

        for (Order order : orderList) {
            Package packageDetails = order.getPackageDetails();
            Payment paymentDetails = paymentRepository.findById(order.getId()).orElse(null);
            report.append(order.getId()).append("\t");
            report.append(order.getUser().getId()).append("\t");
            report.append(order.getSenderAddress()).append("\t");
            report.append(order.getRecipientAddress()).append("\t");
            report.append(order.getDeliveryPreferences()).append("\t");
            report.append(order.getStatus()).append("\t");
            report.append(order.getCreateDate()).append("\t");
            report.append(packageDetails.getId()).append("\t");
            report.append(packageDetails.getWeight()).append("\t");
            report.append(packageDetails.getDimensions()).append("\t");
            report.append(packageDetails.getContents()).append("\t");
            report.append(packageDetails.getTrackingNumber()).append("\t");
            report.append(packageDetails.getStatus()).append("\t");
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
